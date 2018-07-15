package io.readresolve.jpa.business.dao;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import io.readresolve.jpa.business.entities.Vat;

/**
 * Class test over {@code Vat} class.
 * <p>
 * Examples of CRUD operations.
 */
public class VatDaoTest extends DaoBaseTest {

    private final EntityDao dao = new EntityDao(EMF);

    /**
     * Test retrieve.
     */
    @Test
    public void shouldRetrieve() {
	Vat expected = dao.find(Vat.class, Integer.valueOf(1));
	assertNotNull(expected);
    }

    /**
     * Test update.
     */
    @Test
    public void shouldUpdate() {
	Integer id = Integer.valueOf(3);
	LocalDate now = LocalDate.now();
	Vat original = dao.find(Vat.class, id);
	Vat vat = Vat.Builder.forUpdate(original).setEnd(now).build();
	dao.update(vat);
	LocalDate expected = dao.find(Vat.class, id).getEnd();
	assertEquals(expected, now);
    }

    /**
     * Test delete.
     */
    @Test
    public void shouldDelete() {
	Integer id = Integer.valueOf(4);
	Vat original = dao.find(Vat.class, id);
	dao.remove(original);
	Vat expected = dao.find(Vat.class, id);
	assertNull(expected);
    }

    /**
     * Test create.
     */
    @Test
    public void shouldCreate() {
	Vat original = new Vat.Builder().setStart(LocalDate.now())
		.setRate(BigDecimal.TEN).build();
	dao.persist(original);
	assertNotNull(original.getId());
    }
}
