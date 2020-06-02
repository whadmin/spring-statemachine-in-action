package com.wuhao.engine.service;

import com.wuhao.engine.context.EngineContext;
import com.wuhao.engine.event.TestEvents;
import com.wuhao.engine.interceptor.OnNextActionInterceptor;
import com.wuhao.engine.status.TestStates;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wuhao.w
 * @Date: 2020/6/2 14:06
 */
@Service
public class StateMachineStrategyServiceImpl implements BeanFactoryAware, DisposableBean, StateMachineStrategyService<TestStates, TestEvents> {

    private final static Log log = LogFactory.getLog(DefaultStateMachineService.class);

    private BeanFactory beanFactory;

    private final Map<String, StateMachine<TestStates, TestEvents>> businessMachines = new HashMap<String, StateMachine<TestStates, TestEvents>>();

    @Autowired
    private StateMachinePersist<TestStates, TestEvents, String> stateMachinePersist;

    @Autowired
    private OnNextActionInterceptor onNextActionInterceptor;

    @Override
    public StateMachine<TestStates, TestEvents> acquireStateMachine(String businessId, String businessIdentity, EngineContext engineContext) {
        return acquireStateMachine(businessId, businessIdentity, engineContext, true);
    }

    @Override
    public StateMachine<TestStates, TestEvents> acquireStateMachine(String businessId, String businessIdentity, EngineContext engineContext, boolean start) {
        StateMachineFactory<TestStates, TestEvents> stateMachineFactory = beanFactory.getBean(businessIdentity, StateMachineFactory.class);
        synchronized (businessMachines) {
            StateMachine<TestStates, TestEvents> stateMachine = businessMachines.get(businessId);
            if (stateMachine == null) {
                stateMachine = stateMachineFactory.getStateMachine(businessId);
                if (stateMachinePersist != null) {
                    try {
                        StateMachineContext<TestStates, TestEvents> stateMachineContext = stateMachinePersist.read(businessId);
                        stateMachine = restoreStateMachine(stateMachine, stateMachineContext);
                    } catch (Exception e) {
                        log.error("Error handling context", e);
                        throw new StateMachineException("Unable to read context from store", e);
                    }
                    stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {
                        @Override
                        public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                            function.addStateMachineInterceptor((StateMachineInterceptor<TestStates, TestEvents>) onNextActionInterceptor);
                        }
                    });
                }
                businessMachines.put(businessId, stateMachine);
                if(Objects.nonNull(stateMachine.getState())){
                    engineContext.setSource(stateMachine.getState().getId());
                }
            }
            return handleStart(stateMachine, start);
        }
    }

    @Override
    public void releaseStateMachine(String businessId) {
        log.info("Releasing machine with id " + businessId);
        synchronized (businessMachines) {
            StateMachine<TestStates, TestEvents> stateMachine = businessMachines.remove(businessId);
            if (stateMachine != null) {
                log.info("Found machine with id " + businessId);
                stateMachine.stop();
            }
        }
    }

    @Override
    public void releaseStateMachine(String businessId, boolean stop) {
        log.info("Releasing machine with id " + businessId);
        synchronized (businessMachines) {
            StateMachine<TestStates, TestEvents> stateMachine = businessMachines.remove(businessId);
            if (stateMachine != null) {
                log.info("Found machine with id " + businessId);
                handleStop(stateMachine, stop);
            }
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void destroy() throws Exception {

    }

    protected StateMachine<TestStates, TestEvents> restoreStateMachine(StateMachine<TestStates, TestEvents> stateMachine, final StateMachineContext<TestStates, TestEvents> stateMachineContext) {
        if (stateMachineContext == null) {
            return stateMachine;
        }
        stateMachine.stop();
        // only go via top region
        stateMachine.getStateMachineAccessor().doWithRegion(new StateMachineFunction<StateMachineAccess<TestStates, TestEvents>>() {

            @Override
            public void apply(StateMachineAccess<TestStates, TestEvents> function) {
                function.resetStateMachine(stateMachineContext);
            }
        });
        return stateMachine;
    }

    protected StateMachine<TestStates, TestEvents> handleStart(StateMachine<TestStates, TestEvents> stateMachine, boolean start) {
        if (start) {
            if (!((Lifecycle) stateMachine).isRunning()) {
                StateMachineStrategyServiceImpl.StartListener<TestStates, TestEvents> listener = new StateMachineStrategyServiceImpl.StartListener<>(stateMachine);
                stateMachine.addStateListener(listener);
                stateMachine.start();
                try {
                    listener.latch.await();
                } catch (InterruptedException e) {
                }
            }
        }
        return stateMachine;
    }

    protected StateMachine<TestStates, TestEvents> handleStop(StateMachine<TestStates, TestEvents> stateMachine, boolean stop) {
        if (stop) {
            if (((Lifecycle) stateMachine).isRunning()) {
                StateMachineStrategyServiceImpl.StopListener<TestStates, TestEvents> listener = new StateMachineStrategyServiceImpl.StopListener<>(stateMachine);
                stateMachine.addStateListener(listener);
                stateMachine.stop();
                try {
                    listener.latch.await();
                } catch (InterruptedException e) {
                }
            }
        }
        return stateMachine;
    }

    private static class StartListener<TestStates, TestEvents> extends StateMachineListenerAdapter<TestStates, TestEvents> {

        final CountDownLatch latch = new CountDownLatch(1);
        final StateMachine<TestStates, TestEvents> stateMachine;

        public StartListener(StateMachine<TestStates, TestEvents> stateMachine) {
            this.stateMachine = stateMachine;
        }

        @Override
        public void stateMachineStarted(StateMachine<TestStates, TestEvents> stateMachine) {
            this.stateMachine.removeStateListener(this);
            latch.countDown();
        }
    }

    private static class StopListener<TestStates, TestEvents> extends StateMachineListenerAdapter<TestStates, TestEvents> {

        final CountDownLatch latch = new CountDownLatch(1);
        final StateMachine<TestStates, TestEvents> stateMachine;

        public StopListener(StateMachine<TestStates, TestEvents> stateMachine) {
            this.stateMachine = stateMachine;
        }

        @Override
        public void stateMachineStopped(StateMachine<TestStates, TestEvents> stateMachine) {
            this.stateMachine.removeStateListener(this);
            latch.countDown();
        }
    }
}
