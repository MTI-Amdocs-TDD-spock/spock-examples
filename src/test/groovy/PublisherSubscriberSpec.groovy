/*
 * Copyright 2009 the original author or authors.
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


import org.spockframework.util.Nullable
import spock.lang.Ignore
import spock.lang.IgnoreRest
import spock.lang.Specification

class Publisher {
  def subscribers = []

  def send(event) {
    subscribers.each {
      try {
        it.receive(event)
      } catch (Exception e) {}
    }
  }
}

interface Subscriber {
  def receive(event)
}

class SubscriberImpl {

  def receive(event){
    if(event.equals("message1")){
      return "ok"
    }else{
      return "not ok"
    }
  }
}

class PublisherSpec extends Specification {
  def pub
  def sub1
  def sub2

  def setup(){
    pub = new Publisher()
    sub1 = Mock(Subscriber)
    sub2 = Mock(Subscriber)
  }

  def "delivers events to all subscribers"() {
    given:
    pub.subscribers << sub1 << sub2

    when:
    pub.send("event")

    then:
    1 * sub1.receive("event")
    1 * sub2.receive("event")

    cleanup:
    pub.subscribers = []
  }

  def "can cope with misbehaving subscribers"() {
    given:
    pub.subscribers << sub1 << sub2
    sub1.receive(_) >> { throw new Exception() }

    when:
    pub.send("event1")
    pub.send("event2")

    then:
    1 * sub2.receive("event1")
    1 * sub2.receive("event2")
  }

/*  This test shows that Spock, internally, runs interactions
    in the then: block before the when: block AND before any
    variable assignment or regular method() call regardless
    of order
  */
  @Ignore
  def "spock runs the then: interaction before its when:"(){
    when:
    pub.send("hello")

    then:
    def message = "hello"
    1 * sub1.receive(message)

    when:
    pub.send("hello")

    then:
    def message1 = "hello"
    1 * sub1.receive(message1)
//    interaction {
//      def message1 = "hello"
//      1 * sub1.receive(message1)
//    }
  }

 // Mock method stubbing example. See how it doesn't use a real object since it
  // returns null not the string we expect i.e 'not ok'
  def "Mock stubbed method is not called on the implementation of a class"(){

    given:"A stubbed method on a Subscriver"
    SubscriberImpl subscriber = Mock()

    expect:"call to subscriber.receive to not call method on impplementation and return null"
    subscriber.receive("not ok at all").equals(null)

//    when:
//    subscriber.receive(_) >> "not ok"
//
//    then:
//    subscriber.receive("not ok at all") == "not ok"
  }

  // Stub method stubbing example. See how it doesn't use a real object since it
  // returns null not the string we expect i.e 'not ok'
  def "Stub does not call methods on the implementation of a class"(){

    given:"A Stub of SubscriberImpl"
    SubscriberImpl subscriber = Stub()

    when:"we call receive() on subscriber"
    def sub = subscriber.receive("not ok at all")

    then:"the expected return value 'not ok' is not returned"
    sub != "not ok"

//    when:"we stub subscriber to return 'not ok' if message contains 'not' and 'ok' otherwise"
//    subscriber.receive(_) >> {String message -> message.contains("not")?"not ok":"ok"}
//
//    then:"receive returns 'not ok' when the parameter contains 'not'"
//    subscriber.receive("not ok at all") == "not ok"
//    and:"receive returns 'ok' when the parameter dosnt contain 'not'"
//    subscriber.receive("foo") == "ok"

  }

  def "All spies are based on real object"(){
    def subscriber

    given:"a Spy of a subscriber implementation"
    subscriber = Spy(SubscriberImpl)

    and:"the subscriber is added to the publisher"
    pub.subscribers << subscriber

    when:"publisher sends message1 to subscriber"
    pub.send("message1")

    then: "recieve is called once on subscriber with arg 'message1'"
    1 * subscriber.receive("message1")

    and:"it returns 'ok'"
    subscriber.receive("message1") == "ok"

    when:"publisher sends 'not message1' to subscriber"
    pub.send("not message1")

    then: "recieve is called on subscriber with 'not message1' as arg"
    1*subscriber.receive("not message1")
    and:"it returns not ok"
    subscriber.receive(_) == "not ok"


  }
}
