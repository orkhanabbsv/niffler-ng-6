package guru.qa.niffler.api.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import okhttp3.ResponseBody;
import org.w3c.dom.Document;
import retrofit2.Converter;

import javax.xml.stream.XMLInputFactory;
import java.io.IOException;
import java.io.InputStream;

final class SoapResponseConverter<T> implements Converter<ResponseBody, T> {
    final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    final JAXBContext context;
    final Class<T> type;

    SoapResponseConverter(JAXBContext context, Class<T> type) {
        this.context = context;
        this.type = type;

        // Prevent XML External Entity attacks (XXE).
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try(value; InputStream is = value.byteStream()) {
            MimeHeaders headers = new MimeHeaders();
            if(value.contentType() != null) {
                headers.addHeader("Content-Type", value.contentType().toString());
            }

            SOAPMessage response = MessageFactory.newInstance().createMessage(
                    headers,
                    is
            );
            Document document = response.getSOAPBody().extractContentAsDocument();
            return context.createUnmarshaller().unmarshal(document, type).getValue();
        } catch (SOAPException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}