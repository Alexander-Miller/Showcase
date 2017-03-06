package org.educama.shipment.boundary;

import org.educama.shipment.api.datastructure.ShipmentTaskDS;

import java.util.List;

    public interface ShipmentTaskBoundaryService {
    /**
     * Retrieves all tasks.
     * 
     * @return a collection of all tasks
     */
     List<ShipmentTaskDS> findAll();
}
