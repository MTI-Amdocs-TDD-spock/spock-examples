import spock.lang.Specification

class StringSpec extends Specification{
  def "given string is empty size of string is zero"(){

      when: "To express change of state"
      String s = ""

      then:"size is zero"
      s.size() == 0
  }

    def "given string is not empty the size returned is correct"(){
        when:
        String  s = "abcd"

        then:
        s.size() == 4

    }

    def "given string abcdefg then charAt position 3 is d"(){
        given:
        String s = "abcdefg"

        when:
        s.charAt(3)

        then:
        s.charAt(3) == "d"
    }
}
