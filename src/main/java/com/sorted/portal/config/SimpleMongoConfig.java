package com.sorted.portal.config;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.Decimal128;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.NonNull;

@Configuration
public class SimpleMongoConfig {

	@Bean
	public MongoClient mongo() {
		final ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/");
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		final MongoClientSettings mongoClientSettings = MongoClientSettings.builder().codecRegistry(pojoCodecRegistry)
				.applyConnectionString(connectionString).build();
		return MongoClients.create(mongoClientSettings);
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		MongoTemplate mongoTemplate = new MongoTemplate(mongo(), "sorted");
		mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
		MappingMongoConverter conv = (MappingMongoConverter) mongoTemplate.getConverter();
		conv.setCustomConversions(mongoCustomConversions());
		conv.afterPropertiesSet();

		System.out.println("MongoTemplate connected to database: " + mongoTemplate.getDb().getName());

		return mongoTemplate;
	}

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return new MongoCustomConversions(
				Arrays.asList(new BigDecimalDecimal128Converter(), new Decimal128BigDecimalConverter()));

	}

	@WritingConverter
	private static class BigDecimalDecimal128Converter implements Converter<BigDecimal, Decimal128> {
		@Override
		public Decimal128 convert(@NonNull BigDecimal source) {
			return new Decimal128(source.setScale(2, RoundingMode.HALF_UP));
		}
	}

	@ReadingConverter
	private static class Decimal128BigDecimalConverter implements Converter<Decimal128, BigDecimal> {

		@Override
		public BigDecimal convert(@NonNull Decimal128 source) {
			return source.bigDecimalValue() == null ? null : source.bigDecimalValue().setScale(2, RoundingMode.HALF_UP);
		}
	}

}
