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

import spock.lang.*

@Unroll
class DataDrivenSpec extends Specification {
  def "maximum of two numbers"() {
    expect:
    Math.max(a, b) == c

    where:
    a << [3, 5, 9]
    b << [7, 4, 9]
    c << [7, 5, 9]
  }

  def "minimum of #a and #b is #c"() {
    expect:
    Math.min(a, b) == c

    where:
    a | b || c
    3 | 7 || 3
    5 | 4 || 4
    9 | 9 || 9
  }

  def "#person.name is a #sex.toLowerCase() person"() {
    expect:
    person.getSex() == sex

    where:
    person                      || sex
    new Person(name: "Fred")    || "Male"
    new Person(name: "Wilma")   || "Female"
  }

    def "person age is changed to #age"(){
        given:"A person"
        def p = new Person(name: name)

        when:"we we set person's age ${age}"
        p.setAge(age)

        then:"we get the correct age"
        p.age == expectedAge

        where:

        name    |   age ||  expectedAge
        "Fred"  |   33  ||  33
        "Tammi" |   33  ||  30
    }

    def "getAge return age for men"(){
        given:"A Person"
        def person = new Person()

        when:"person is a man"
        person.name = "Fred"

        and:"his age is 20"
        person.age = 20

        then:"getAge to return 20"
        person.getAge() == 20

        when:"person is a woman"
        person.name = "Not Fred"
        and: "her age is 20"

        person.age = 20
        then:"getAge to return 17"
        person.getAge() == 17
    }

    def "Return correct age for man and fixed age for woman"(){
        given:
        def Person person

        when:
        person = new Person(name: name, age: age)

        then:
        person.getAge() == expectedAge

        where:
        name    |   age ||   expectedAge
        "Fred"  |   33  ||  33
        "Tammi" |   33  ||  30
    }



 class Person {
    String name
    private int age

    String getSex() {
      name == "Fred" ? "Male" : "Female"
    }

    void setAge(int age){
       this.age = age
    }

     int getAge(){
         if(getSex().equals("Male")){
             return age
         }else if(getSex().equals("Female")){
             return (age - 3)
         }
         return -1;
     }
  }
}