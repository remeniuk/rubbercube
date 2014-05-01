package com.bokland.rubbercube

/**
 * Created by remeniuk on 4/29/14.
 */
case class Dimension(fieldName: String, cubeId: Option[String] = None,
                     alias: Option[String] = None) {

  val name = alias.getOrElse(fieldName.replaceAll("\\.", "_"))

}
