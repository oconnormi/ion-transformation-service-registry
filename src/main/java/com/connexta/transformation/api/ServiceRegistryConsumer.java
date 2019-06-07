/**
 * Copyright (c) Connexta
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package com.connexta.transformation.api;

import java.io.IOException;

/** Represents an AMQP consumer. */
public interface ServiceRegistryConsumer {

  /**
   * Consumes an AMQP message from a queue that is stored as a byte array format.
   *
   * @param transformRequest an AMQP message on the queue representing a transform request
   */
  void consumeFromQueue(byte[] transformRequest) throws IOException;
}
