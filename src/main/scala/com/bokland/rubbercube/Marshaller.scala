package com.bokland.rubbercube

/**
 * Created by remeniuk on 4/29/14.
 */
trait Marshaller[From, To] {

  def marshal(obj: From): To

}
