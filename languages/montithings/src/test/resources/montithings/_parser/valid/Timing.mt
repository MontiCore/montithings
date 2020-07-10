/* (c) https://github.com/MontiCore/monticore */
package valid;

/**
 * Valid model.
 */
component Timing {

    component TimedInner {
        timing instant;
    }

    component TimeSyncInner {
        timing sync;
    }

    component UntimedInner {
        timing untimed;
    }

}
