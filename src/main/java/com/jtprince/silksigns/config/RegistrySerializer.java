package com.jtprince.silksigns.config;

import de.exlll.configlib.Serializer;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public class RegistrySerializer<T extends Keyed> implements Serializer<T, String> {
    private final Registry<T> registry;
    private final String name;

    public RegistrySerializer(Registry<T> registry, String name) {
        this.registry = registry;
        this.name = name;
    }

    @Override
    public String serialize(T element) {
        try {
            return element.key().asMinimalString();
        } catch (NoSuchMethodError e) {
            // asMinimalString was added around 1.20.2, fall back on the old method
            return element.key().toString();
        }
    }

    @Override
    public T deserialize(String element) {
        NamespacedKey key = NamespacedKey.fromString(element);
        if (key == null) {
            throw new IllegalArgumentException("Invalid " + name + ": " + element);
        }

        T obj = registry.get(key);
        if (obj == null) {
            throw new IllegalArgumentException("Invalid " + name + ": " + element);
        }

        return obj;
    }
}
