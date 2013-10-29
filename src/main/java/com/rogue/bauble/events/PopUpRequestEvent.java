package com.rogue.bauble.events;

import static com.google.common.base.Preconditions.*;
import com.rogue.bauble.widgets.PopUp;

/**
 * Event triggered when an object who does not control high level rendering
 * needs to request that a pop-up be displayed.
 * 
 * @author R. Matt McCann
 */
public class PopUpRequestEvent {
    private final PopUp popUp;
    
    public PopUpRequestEvent(PopUp popUp) {
        this.popUp = checkNotNull(popUp);
    }
    
    public PopUp getPopUp() { return popUp; }
}
