package com.app.book.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JsonbContextResolver implements ContextResolver<Jsonb> {

	@Inject
    @ConfigProperty(name = "jsonb.formatting", defaultValue = "false")
    boolean isFormattingEnabled;

    @Override
    public Jsonb getContext(Class<?> type) {
        // Helidon injects the value based on the active profile
        var config = new JsonbConfig().withFormatting(isFormattingEnabled);
        return JsonbBuilder.create(config);
    }
}
