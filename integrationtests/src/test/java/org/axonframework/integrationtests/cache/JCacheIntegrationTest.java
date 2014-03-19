/*
 * Copyright (c) 2010-2014. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.integrationtests.cache;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.cache.CacheUtils;
import org.junit.*;
import org.junit.runner.*;
import org.ops4j.pax.exam.util.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.atomic.AtomicLong;
import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;

import static org.junit.Assert.*;

/**
 * @author Allard Buijze
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/META-INF/spring/infrastructure-context.xml",
        "/META-INF/spring/caching-repository-context.xml"})
@Transactional
public class JCacheIntegrationTest {

    @Autowired
    private Cache jCache;

    @Autowired
    private CommandGateway commandGateway;

    @Test
    public void testEntriesEvictedOnProcessingException() throws Exception {
        final AtomicLong counter = new AtomicLong();
        CacheUtils.registerCacheEntryListener(jCache, new CacheEntryRemovedListener() {
            @Override
            public void entryRemoved(CacheEntryEvent event) throws CacheEntryListenerException {
                counter.incrementAndGet();
            }
        });
        commandGateway.send(new TestAggregateRoot.CreateCommand("1234"));

        assertTrue(jCache.containsKey("1234"));

        commandGateway.send(new TestAggregateRoot.FailCommand("1234"), new CommandCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                fail("Command should not have succeeded");
            }

            @Override
            public void onFailure(Throwable cause) {
//                These lines prove that JCache doesn't work properly..
                assertTrue("Got an exception, as expected, but it seems to be the wrong one: " + cause.getClass()
                                                                                                      .getName(),
                           cause.getMessage().contains("I don't like this"));
            }
        });
        assertFalse(jCache.containsKey("1234"));
        assertEquals(1, counter.get());
    }

    @Test
    public void testEntriesNeverStoredOnProcessingException() throws Exception {
        commandGateway.send(new TestAggregateRoot.FailingCreateCommand("1234"));
        assertFalse(jCache.containsKey("1234"));
    }
}
