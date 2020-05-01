package de.wolfsvl.copper2go.workflow;

import de.wolfsvl.copper2go.impl.ContextStoreImpl;
import de.wolfsvl.copper2go.workflowapi.Context;
import de.wolfsvl.copper2go.workflowapi.ContextStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
public class MapperTest {

    private Mapper mapper;

    @BeforeEach
    public void beforeEach(){
        this.mapper = new Mapper();
    }

    @Test
    public void mapRequestTest() {
        final ContextStore contextStore = new ContextStoreImpl();
        Context context = new Context("Wolf S");
        contextStore.store("uuid", context);
        var context2 = new HelloContext("uuid", contextStore);
        mapper.mapRequest(context2);
        //context2.name
        assertThat(context2.name).isEqualTo("Wolf");
    }

    @Test
    public void mapResponseTest() {
        final ContextStore contextStore = new ContextStoreImpl();
        Context context = new Context("Wolf S");
        contextStore.store("uuid", context);
        var context2 = new HelloContext("uuid", contextStore);
        context2.price = 0;
        context2.name = "W";
        mapper.mapResponse(context2);
        assertThat(context2.response).isEqualTo("Hello W! Please transfer 0 cent");
    }
}