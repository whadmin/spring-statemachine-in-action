package com.wuhao.statemachine.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.Lifecycle;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wuhao.w
 * @Date: 2019/11/24 14:07
 */
@Service
public class StateMachineStrategyService<S, E> implements BeanFactoryAware, DisposableBean {

    private final static Log log = LogFactory.getLog(DefaultStateMachineService.class);

    private BeanFactory beanFactory;

    private final Map<String, StateMachine<S, E>> businessMachines = new HashMap<String, StateMachine<S, E>>();

    private final Map<String, StateMachine<S, E>> processMachines = new HashMap<String, StateMachine<S, E>>();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    @Override
    public final void destroy() throws Exception {
        doStop();
    }

    public StateMachine<S, E> acquireBusinessStateMachine(String businessId, String businessIdentity, S previousStatus, boolean start){
        StateMachineFactory<S, E> stateMachineFactory = beanFactory.getBean(businessIdentity, StateMachineFactory.class);
        synchronized (businessMachines) {
            StateMachine<S,E> stateMachine = businessMachines.get(businessId);
            if (stateMachine == null) {
                stateMachine = stateMachineFactory.getStateMachine(businessId);
                StateMachineContext<S, E> stateMachineContext =new DefaultStateMachineContext<>(previousStatus, null, null, null, null, stateMachine.getId());
                stateMachine = restoreStateMachine(stateMachine, stateMachineContext);
                businessMachines.put(businessId, stateMachine);
            }
            return handleStart(stateMachine, start);
        }
    }

    public StateMachine<S, E> acquireProcessStateMachine(String businessId, String processID, S previousStatus, boolean start){
        StateMachineFactory<S, E> stateMachineFactory = beanFactory.getBean(processID, StateMachineFactory.class);
        synchronized (processMachines) {
            StateMachine<S,E> stateMachine = processMachines.get(businessId);
            if (stateMachine == null) {
                stateMachine = stateMachineFactory.getStateMachine(businessId);
                StateMachineContext<S, E> stateMachineContext =new DefaultStateMachineContext<>(previousStatus, null, null, null, null, stateMachine.getId());
                stateMachine = restoreStateMachine(stateMachine, stateMachineContext);
                processMachines.put(businessId, stateMachine);
            }
            return handleStart(stateMachine, start);
        }
    }

    public void releaseBusinessStateMachine(String businessId, boolean stop) {
        synchronized (businessMachines) {
            StateMachine<S, E> stateMachine = businessMachines.remove(businessId);
            if (stateMachine != null) {
                handleStop(stateMachine, stop);
            }
        }
    }

    public void releaseProcessStateMachine(String businessId, boolean stop) {
        synchronized (processMachines) {
            StateMachine<S, E> stateMachine = processMachines.remove(businessId);
            if (stateMachine != null) {
                handleStop(stateMachine, stop);
            }
        }
    }

    protected void doStop() {
        log.info("Entering stop sequence, stopping all managed machines");
        synchronized (businessMachines) {
            ArrayList<String> machineIds = new ArrayList<>(businessMachines.keySet());
            for (String machineId : machineIds) {
                releaseBusinessStateMachine(machineId, true);
            }
        }
        synchronized (processMachines) {
            ArrayList<String> machineIds = new ArrayList<>(processMachines.keySet());
            for (String machineId : machineIds) {
                releaseProcessStateMachine(machineId, true);
            }
        }
    }

    protected StateMachine<S, E> handleStart(StateMachine<S, E> stateMachine, boolean start) {
        if (start) {
            if (!((Lifecycle) stateMachine).isRunning()) {
                StateMachineStrategyService.StartListener<S, E> listener = new StartListener<>(stateMachine);
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

    protected StateMachine<S, E> handleStop(StateMachine<S, E> stateMachine, boolean stop) {
        if (stop) {
            if (((Lifecycle) stateMachine).isRunning()) {
                StopListener<S, E> listener = new StopListener<>(stateMachine);
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

    protected StateMachine<S, E> restoreStateMachine(StateMachine<S, E> stateMachine, final StateMachineContext<S, E> stateMachineContext) {
        if (stateMachineContext == null) {
            return stateMachine;
        }
        stateMachine.stop();
        stateMachine.getStateMachineAccessor().doWithAllRegions(new StateMachineFunction<StateMachineAccess<S, E>>() {
            @Override
            public void apply(StateMachineAccess<S, E> function) {
                function.resetStateMachine(stateMachineContext);
            }
        });
        return stateMachine;
    }

    private static class StartListener<S, E> extends StateMachineListenerAdapter<S, E> {

        final CountDownLatch latch = new CountDownLatch(1);
        final StateMachine<S, E> stateMachine;

        public StartListener(StateMachine<S, E> stateMachine) {
            this.stateMachine = stateMachine;
        }

        @Override
        public void stateMachineStarted(StateMachine<S, E> stateMachine) {
            this.stateMachine.removeStateListener(this);
            latch.countDown();
        }
    }

    private static class StopListener<S, E> extends StateMachineListenerAdapter<S, E> {

        final CountDownLatch latch = new CountDownLatch(1);
        final StateMachine<S, E> stateMachine;

        public StopListener(StateMachine<S, E> stateMachine) {
            this.stateMachine = stateMachine;
        }

        @Override
        public void stateMachineStopped(StateMachine<S, E> stateMachine) {
            this.stateMachine.removeStateListener(this);
            latch.countDown();
        }
    }
}
