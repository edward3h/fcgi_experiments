package org.ethelred.experiments
import com.agorapulse.gru.*
import com.agorapulse.gru.minions.*
import com.agorapulse.gru.http.Http
import org.junit.Rule
import spock.lang.*

class RequestsSpec extends Specification {
    @Rule Gru<Http> gru = Gru.equip(Http.steal(this))                                   
                             .prepare('http://fcgi-experiments')

    @Unroll
    def "get request #page to contain #expectedText"(page, expectedText) {
        expect:
            gru.test {
                get page
                expect {
                    def content = inline(expectedText)
                    command(RegexpMinion.class, m -> m.setResponseContent(content))
                }
            }

        where:
            page | expectedText
            "/hello_cgi/hello_perl.cgi" | "Hello World!"
            "/hello_cgi/hello_java.cgi" | "Hello, world!\n"
            "/hello_fcgi/counter_perl.fcgi" | /I have run \d+ times./
            "/hello_fcgi/hello_java.fcgi" | /Something something/
    }                         
}

class RegexpMinion extends AbstractContentMinion<Client> {

    final int index = TEXT_MINION_INDEX

    RegexpMinion() {
        super(Client)
    }

    protected void similar(String actual, String expected) throws AssertionError {
        assert actual ==~ expected
    }
}