package fun.bb1.events.abstraction.bukkit;

import org.jetbrains.annotations.NotNull;

import fun.bb1.objects.annotations.DisallowsEmptyString;

public abstract class StaticEvent<I extends StaticEvent<I>> {
	
    private final boolean async;
    
    public StaticEvent() {
    	this(false);
    }
    
    public StaticEvent(final boolean isAsync) {
    	this.async = false; // Not supported yet
    }
    
    @NotNull
    public abstract IHandlerList<I> getHandlers();
	
    public final @NotNull @DisallowsEmptyString String getEventName() {
        return this.getClass().getSimpleName();
    }
    
    public final boolean isAsynchronous() {
    	return this.async;
    }
    
}
