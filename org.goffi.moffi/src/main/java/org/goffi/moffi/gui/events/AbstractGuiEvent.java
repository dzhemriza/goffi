package org.goffi.moffi.gui.events;

public abstract class AbstractGuiEvent implements GuiEvent {

    private final Object sender;

    protected AbstractGuiEvent(Object sender) {
        this.sender = sender;
    }

    @Override
    public Object getSender() {
        return sender;
    }
}
