package com.minenash.customhud.HudElements.interfaces;

import com.minenash.customhud.HudElements.functional.FunctionalElement;

public interface ExecuteElement {

    void run();

    class ArbritaryExecuteElement extends FunctionalElement implements ExecuteElement {

        private final Runnable runnable;

        public ArbritaryExecuteElement(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }

}
