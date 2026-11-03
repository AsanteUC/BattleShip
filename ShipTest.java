import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipTest
{
    @Test
    void storesNameAndSize()
    {
        Ship ship = new Ship("Cruiser", 3);
        assertEquals("Cruiser", ship.getName());
        assertEquals(3, ship.getSize());
    }

    @Test
    void newShipIsNotSunk()
    {
        assertFalse(new Ship("Destroyer", 2).isSunk());
    }

    @Test
    void sinksAfterOneHitPerCell()
    {
        Ship ship = new Ship("Destroyer", 2);
        ship.registerHit();
        assertFalse(ship.isSunk());
        ship.registerHit();
        assertTrue(ship.isSunk());
    }

    @Test
    void extraHitsDoNotCountPastTheSize()
    {
        Ship ship = new Ship("Destroyer", 2);
        ship.registerHit();
        ship.registerHit();
        ship.registerHit();
        assertEquals(2, ship.getHits());
    }

    @Test
    void remembersItsCells()
    {
        Ship ship = new Ship("Destroyer", 2);
        ship.addCell(4, 4);
        ship.addCell(4, 5);
        assertEquals(2, ship.getCells().size());
        assertEquals(4, ship.getCells().get(1)[0]);
        assertEquals(5, ship.getCells().get(1)[1]);
    }
}
