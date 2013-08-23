/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.google.code.morphia;


import java.util.Date;
import java.util.List;

import org.junit.Test;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.dao.DAO;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.testdaos.HotelDAO;
import com.google.code.morphia.testmodel.Address;
import com.google.code.morphia.testmodel.Hotel;
import com.google.code.morphia.testmodel.PhoneNumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * @author Olafur Gauti Gudmundsson
 */
public class TestDAO extends TestBase {

  @Test
  public void testNewDAO() throws Exception {
    morphia.map(Hotel.class);

    final DAO<Hotel, String> hotelDAO = new BasicDAO<Hotel, String>(Hotel.class, mongo, morphia, "morphia_test");

    final Hotel borg = Hotel.create();
    borg.setName("Hotel Borg");
    borg.setStars(4);
    borg.setTakesCreditCards(true);
    borg.setStartDate(new Date());
    borg.setType(Hotel.Type.LEISURE);
    final Address address = new Address();
    address.setStreet("Posthusstraeti 11");
    address.setPostCode("101");
    borg.setAddress(address);

    hotelDAO.deleteByQuery((Query<Hotel>) hotelDAO.find());
    hotelDAO.save(borg);
    assertEquals(1, hotelDAO.count());
    assertNotNull(borg.getId());

    final Hotel hotelLoaded = hotelDAO.get(borg.getId());
    assertEquals(borg.getName(), hotelLoaded.getName());
    assertEquals(borg.getAddress().getPostCode(), hotelLoaded.getAddress().getPostCode());

    final Hotel hotelByValue = hotelDAO.findOne("name", "Hotel Borg");
    assertNotNull(hotelByValue);
    assertEquals(borg.getStartDate(), hotelByValue.getStartDate());

    assertTrue(hotelDAO.exists("stars", 4));

    final Hotel hilton = Hotel.create();
    hilton.setName("Hilton Hotel");
    hilton.setStars(4);
    hilton.setTakesCreditCards(true);
    hilton.setStartDate(new Date());
    hilton.setType(Hotel.Type.BUSINESS);
    final Address hiltonAddress = new Address();
    hiltonAddress.setStreet("Some street 44");
    hiltonAddress.setPostCode("101");
    hilton.setAddress(hiltonAddress);
    hilton.getPhoneNumbers().add(new PhoneNumber(354, 1234567, PhoneNumber.Type.PHONE));

    hotelDAO.save(hilton);

    final List<Hotel> allHotels = hotelDAO.find().asList();
    assertEquals(2, allHotels.size());
    assertEquals(2, hotelDAO.findIds().size());

    assertEquals(1, hotelDAO.find(hotelDAO.createQuery().offset(1).limit(10)).asList().size());
    assertEquals(1, hotelDAO.find(hotelDAO.createQuery().limit(1)).asList().size());
    assertTrue(hotelDAO.exists("type", Hotel.Type.BUSINESS));
    assertNotNull(hotelDAO.findOne("type", Hotel.Type.LEISURE));

    assertEquals(0, hotelDAO.count(hotelDAO.createQuery().field("stars").notEqual(4)));
    assertEquals(2, hotelDAO.count(hotelDAO.createQuery().field("stars").lessThan(5)));
    assertEquals(2, hotelDAO.count(hotelDAO.createQuery().field("stars").greaterThanOrEq(4)));
    assertEquals(2, hotelDAO.count(hotelDAO.createQuery().field("stars").lessThan(5)));
    assertEquals(1, hotelDAO.count(hotelDAO.createQuery().field("phoneNumbers").sizeEq(1)));
    assertEquals(2, hotelDAO.count(hotelDAO.createQuery().filter("stars", 4).order("address.address_street")));
    assertEquals(borg.getName(), hotelDAO.find(hotelDAO.createQuery().filter("stars", 4).order("address.address_street")).iterator().next()
      .getName());
    assertEquals(hilton.getName(), hotelDAO.find(hotelDAO.createQuery().filter("stars", 4).order("-address.address_street")).iterator()
      .next().getName());
    assertEquals(hilton.getName(), hotelDAO.find(hotelDAO.createQuery().filter("stars", 4).order("stars, -address.address_street"))
      .iterator().next().getName());

    hotelDAO.deleteById(borg.getId());
    assertEquals(1, hotelDAO.count());

    hotelDAO.getCollection().drop();
    assertEquals(0, hotelDAO.count());
  }

  @Test
  public void testDAO() throws Exception {
    morphia.map(Hotel.class);

    final Hotel borg = Hotel.create();
    borg.setName("Hotel Borg");
    borg.setStars(4);
    borg.setTakesCreditCards(true);
    borg.setStartDate(new Date());
    borg.setType(Hotel.Type.LEISURE);
    final Address address = new Address();
    address.setStreet("Posthusstraeti 11");
    address.setPostCode("101");
    borg.setAddress(address);

    final HotelDAO hotelDAO = new HotelDAO(morphia, mongo);
    hotelDAO.save(borg);
    assertEquals(1, hotelDAO.count());
    assertNotNull(borg.getId());

    final Hotel hotelLoaded = hotelDAO.get(borg.getId());
    assertEquals(borg.getName(), hotelLoaded.getName());
    assertEquals(borg.getAddress().getPostCode(), hotelLoaded.getAddress().getPostCode());

    final Hotel hotelByValue = hotelDAO.findOne("name", "Hotel Borg");
    assertNotNull(hotelByValue);
    assertEquals(borg.getStartDate(), hotelByValue.getStartDate());

    assertTrue(hotelDAO.exists("stars", 4));

    final Hotel hilton = Hotel.create();
    hilton.setName("Hilton Hotel");
    hilton.setStars(4);
    hilton.setTakesCreditCards(true);
    hilton.setStartDate(new Date());
    hilton.setType(Hotel.Type.BUSINESS);
    final Address hiltonAddress = new Address();
    hiltonAddress.setStreet("Some street 44");
    hiltonAddress.setPostCode("101");
    hilton.setAddress(hiltonAddress);

    hotelDAO.save(hilton);

    final List<Hotel> allHotels = hotelDAO.find().asList();
    assertEquals(2, allHotels.size());

    assertEquals(1, hotelDAO.find(hotelDAO.createQuery().offset(1).limit(10)).asList().size());
    assertEquals(1, hotelDAO.find(hotelDAO.createQuery().limit(1)).asList().size());
    assertTrue(hotelDAO.exists("type", Hotel.Type.BUSINESS));
    assertNotNull(hotelDAO.findOne("type", Hotel.Type.LEISURE));

    // try updating
    final UpdateOperations<Hotel> mods = hotelDAO.createUpdateOperations().inc("stars", 1);
    hotelDAO.update(hotelDAO.createQuery().filter("stars", 4), mods);
    assertEquals(2, hotelDAO.count(hotelDAO.createQuery().filter("stars", 5)));

    hotelDAO.deleteById(borg.getId());
    assertEquals(1, hotelDAO.count());

    hotelDAO.getCollection().drop();
    assertEquals(0, hotelDAO.count());
  }

  @Test
  public void testSaveEntityWithId() throws Exception {
    final HotelDAO hotelDAO = new HotelDAO(morphia, mongo);

    final Hotel borg = Hotel.create();
    borg.setName("Hotel Borg");
    borg.setStars(4);
    hotelDAO.save(borg);

    final Hotel hotelLoaded = hotelDAO.get(borg.getId());
    hotelLoaded.setStars(5);
    hotelDAO.save(hotelLoaded);
    final Hotel hotelReloaded = hotelDAO.get(borg.getId());
    assertEquals(5, hotelReloaded.getStars());
  }
}
