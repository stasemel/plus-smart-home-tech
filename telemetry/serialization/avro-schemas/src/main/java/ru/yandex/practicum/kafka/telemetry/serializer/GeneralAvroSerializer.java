package ru.yandex.practicum.kafka.telemetry.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {
    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public byte[] serialize(String s, SpecificRecordBase event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            BinaryEncoder encoder = encoderFactory.binaryEncoder(outputStream, null);
            DatumWriter<SpecificRecordBase> datumWriter = new SpecificDatumWriter<>(event.getSchema());

            // сериализуем данные
            datumWriter.write(event, encoder);

            // сбрасываем все данные из буфера в поток
            encoder.flush();
            // возвращаем сериализованные данные
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new SerializationException(String.format("Ошибка сериализации экземпляра %s", event.getClass()), e);
        }
    }
}
