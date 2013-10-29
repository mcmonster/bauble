package com.rogue.bauble.events;

import static com.google.common.base.Preconditions.*;
import com.rogue.bauble.widgets.PopUp;

/**
 * Event triggered when a pop-up is requested to be closed.
 * 
 * @author R. Matt McCann
 */
public class ClosePopUpEvent {
    private final PopUp popUp;
    
    public ClosePopUpEvent(PopUp popUp) {
        this.popUp = checkNotNull(popUp);
    }
    
    public PopUp getPopUp() { return popUp; }
}
