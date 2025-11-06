/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.privatepractitionerservice.infrastructure.mail.stub;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;

public class MailStubStore {

  private static final String MAILSTUBSTORE = "mailstubstore";
  private Map<String, String> store = new HashMap<>(); // Default hashmap used for unit-tests

  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private RedisConnectionFactory redisConnectionFactory;

  @PostConstruct
  public void init() {
    stringRedisTemplate = new StringRedisTemplate();
    stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
    stringRedisTemplate.afterPropertiesSet();
    store = new DefaultRedisMap<>(MAILSTUBSTORE, stringRedisTemplate);
  }

  public void addMail(String id, String mail) {
    store.put(id, mail);
  }

  public Map<String, String> getMails() {
    return store;
  }

  public void clear() {
    store.clear();
  }
}
