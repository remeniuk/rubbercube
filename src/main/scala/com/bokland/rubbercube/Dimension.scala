package com.bokland.rubbercube

/**
 * Created by remeniuk on 4/29/14.
 */
case class Dimension(_name: String, path: Option[String] = None,
                     cubeId: Option[String] = None, alias: Option[String] = None) {

  def fqn = s"${path.map(_ + ".").getOrElse("")}$name"

  def name = alias.getOrElse(_name)

}
