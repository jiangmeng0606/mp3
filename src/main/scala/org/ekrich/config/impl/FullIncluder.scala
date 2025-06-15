/**
 * Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package org.ekrich.config.impl

import org.ekrich.config.{ConfigIncluder, ConfigIncluderClasspath, ConfigIncluderFile, ConfigIncluderURL}

trait FullIncluder
    extends ConfigIncluder
    with ConfigIncluderFile
    with ConfigIncluderURL
    with ConfigIncluderClasspath {}
