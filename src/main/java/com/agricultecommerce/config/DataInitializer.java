package com.agricultecommerce.config;

import com.agricultecommerce.entity.Category;
import com.agricultecommerce.entity.Product;
import com.agricultecommerce.entity.User;
import com.agricultecommerce.repository.CategoryRepository;
import com.agricultecommerce.repository.ProductRepository;
import com.agricultecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }

        // Seed machinery-only catalog (fresh each run in H2)
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Map<String, Category> categoryMap = new HashMap<>();
        addCategory(categoryMap, "Tractors", "Compact to heavy-duty tractors");
        addCategory(categoryMap, "Tillage & Rotavators", "Rotavators, cultivators, harrows");
        addCategory(categoryMap, "Harvesters", "Combine, reaper binders, harvest tools");
        addCategory(categoryMap, "Drones & Precision Ag", "Spraying drones, mapping, sensors");
        addCategory(categoryMap, "Irrigation Machinery", "Pumps, sprinklers, automation");
        addCategory(categoryMap, "Implements & Attachments", "Loaders, trailers, seeders");
        addCategory(categoryMap, "Spare Parts", "Filters, belts, blades, nozzles");
        addCategory(categoryMap, "Safety & Wearables", "PPE and operator safety gear");

        // Real "source" websites provided by you (we store these as product links)
        final String SRC_TRACTOR_JUNCTION = "https://www.tractorjunction.com/";
        final String SRC_KHETIGAADI = "https://www.khetigaadi.com/";
        final String SRC_AGROSTAR = "https://www.agrostar.in/";
        final String SRC_INDIAMART = "https://www.indiamart.com/agriculture-machinery/";
        final String SRC_TRADEINDIA = "https://www.tradeindia.com/agriculture-machinery.html";
        final String SRC_MAHINDRA = "https://www.mahindratractor.com/";
        final String SRC_SONALIKA = "https://www.sonalika.com/";
        final String SRC_DEERE = "https://www.deere.co.in/";
        final String SRC_VST = "https://www.vsttillers.com/";
        final String SRC_CNH = "https://www.cnhindustrial.com/";
        final String SRC_KUBOTA = "https://www.kubota.com/";
        final String SRC_AGRIEXPO = "https://www.agriexpo.online/";
        final String SRC_MACHINERYPETE = "https://www.machinerypete.com/";
        final String SRC_ALIBABA = "https://www.alibaba.com/agriculture-machinery";
        final String SRC_MADEINCHINA = "https://www.made-in-china.com/agriculture-machinery/";
        final String SRC_FAO = "https://www.fao.org/home/en";

        List<ProductSeed> products = List.of(
                // Tractors
                new ProductSeed("Mahindra 575 DI Tractor (45 HP)", "Popular 2WD tractor for multi-purpose farming.", 82990.00, 12, "Mahindra", "unit", "India", "45 HP, 4-cylinder, 8F+2R gearbox, lifting 1600 kg", null, SRC_MAHINDRA, "Tractors"),
                new ProductSeed("John Deere 5050D Tractor (50 HP)", "Reliable tractor with strong hydraulics.", 109990.00, 8, "John Deere", "unit", "India", "50 HP, 12F+4R, lifting 1600 kg, power steering", null, SRC_DEERE, "Tractors"),
                new ProductSeed("Sonalika DI 750III (55 HP)", "Heavy-duty tractor for large farms.", 119990.00, 6, "Sonalika", "unit", "India", "55 HP, 8F+2R, lifting 2000 kg, oil-immersed brakes", null, SRC_SONALIKA, "Tractors"),
                new ProductSeed("New Holland 3600-2TX (50 HP)", "Comfortable tractor for long working hours.", 112490.00, 7, "New Holland", "unit", "India", "50 HP, 8F+2R, lifting 1700 kg, advanced hydraulics", null, SRC_CNH, "Tractors"),
                new ProductSeed("Kubota MU4501 (45 HP)", "Compact tractor with great fuel economy.", 124990.00, 5, "Kubota", "unit", "Japan", "45 HP, 8F+4R, lifting 1640 kg, ROPS", null, SRC_KUBOTA, "Tractors"),
                new ProductSeed("Eicher 485 (45 HP)", "Affordable tractor with easy maintenance.", 89990.00, 9, "Eicher", "unit", "India", "45 HP, 8F+2R, lifting 1650 kg", null, SRC_TRACTOR_JUNCTION, "Tractors"),

                // Tillage & Rotavators
                new ProductSeed("Rotavator 5 ft (Gear Drive)", "Rotary tiller for seedbed preparation.", 8990.00, 20, "Shaktiman", "unit", "India", "Working width 5 ft, 36 blades, tractor 35-50 HP", null, SRC_AGROSTAR, "Tillage & Rotavators"),
                new ProductSeed("Rotavator 6 ft (Chain Drive)", "High coverage rotavator for medium farms.", 10490.00, 18, "Khedut", "unit", "India", "Working width 6 ft, 42 blades, tractor 45-60 HP", null, SRC_KHETIGAADI, "Tillage & Rotavators"),
                new ProductSeed("Disc Harrow 18 Discs", "Primary tillage implement for residue mixing.", 7490.00, 14, "Fieldking", "unit", "India", "18 discs, disc dia 22 inch, tractor 45+ HP", null, SRC_INDIAMART, "Tillage & Rotavators"),
                new ProductSeed("Cultivator 9 Tynes (Spring Loaded)", "Weeding and soil aeration implement.", 3990.00, 30, "Universal", "unit", "India", "9 tynes, spring loaded, tractor 35-50 HP", null, SRC_TRADEINDIA, "Tillage & Rotavators"),
                new ProductSeed("Power Tiller 8 HP", "Compact tiller for small farms and orchards.", 6490.00, 10, "VST Shakti", "unit", "India", "8 HP diesel, rotary blades, adjustable depth", null, SRC_VST, "Tillage & Rotavators"),

                // Harvesters
                new ProductSeed("Mini Combine Harvester", "Compact harvester for wheat and paddy.", 164990.00, 3, "Kubota", "unit", "Japan", "Cutting width 1.4 m, grain tank 450 L", null, SRC_KUBOTA, "Harvesters"),
                new ProductSeed("Reaper Binder 4 ft", "Efficient harvesting for paddy/wheat.", 12990.00, 7, "Kisan Kraft", "unit", "India", "4 ft cutting width, tractor PTO driven", null, SRC_INDIAMART, "Harvesters"),
                new ProductSeed("Straw Reaper Attachment", "Residue management and straw collection.", 14990.00, 6, "Fieldking", "unit", "India", "PTO driven, heavy-duty rotor, safety clutch", null, SRC_TRADEINDIA, "Harvesters"),

                // Drones & Precision Ag
                new ProductSeed("Agri Spraying Drone 10L", "Autonomous drone for pesticide spraying.", 29990.00, 9, "Aarav Drones", "unit", "India", "10L tank, RTK optional, flight time 12-15 min", null, SRC_AGRIEXPO, "Drones & Precision Ag"),
                new ProductSeed("Agri Spraying Drone 16L", "High capacity drone for faster spraying.", 37990.00, 6, "Garuda Aerospace", "unit", "India", "16L tank, obstacle avoidance, smart route planning", null, SRC_AGROSTAR, "Drones & Precision Ag"),
                new ProductSeed("Field Mapping Drone (RGB)", "Survey & NDVI-ready mapping drone.", 24990.00, 5, "DJI", "unit", "China", "24MP camera, waypoint missions, export orthomosaic", null, SRC_ALIBABA, "Drones & Precision Ag"),
                new ProductSeed("Soil Moisture Sensor (Wireless)", "Sensor for irrigation scheduling.", 790.00, 60, "Netafim", "unit", "Israel", "Capacitive probe, app monitoring, long battery life", null, SRC_FAO, "Drones & Precision Ag"),
                new ProductSeed("GPS Guidance Lightbar", "Assisted guidance for straight-line operations.", 3990.00, 22, "Trimble", "unit", "USA", "Sub-meter guidance, daylight readable display", null, SRC_MACHINERYPETE, "Drones & Precision Ag"),

                // Irrigation Machinery
                new ProductSeed("Diesel Water Pump 5 HP", "Reliable pump for irrigation supply.", 2490.00, 40, "Kirloskar", "unit", "India", "5 HP diesel, head 28 m, discharge 700 L/min", null, SRC_INDIAMART, "Irrigation Machinery"),
                new ProductSeed("Submersible Pump 2 HP", "Borewell submersible pump set.", 3190.00, 25, "Crompton", "unit", "India", "2 HP, 10-stage, copper winding", null, SRC_TRADEINDIA, "Irrigation Machinery"),
                new ProductSeed("Rain Gun Sprinkler (Big)", "High throw sprinkler for large fields.", 1990.00, 35, "Jain Irrigation", "unit", "India", "Throw radius 30-45 m, adjustable jet", null, SRC_AGROSTAR, "Irrigation Machinery"),
                new ProductSeed("Automatic Irrigation Controller", "Smart controller for valves and pumps.", 1290.00, 50, "Hunter", "unit", "USA", "8-zone, app control, weather-based schedules", null, SRC_AGRIEXPO, "Irrigation Machinery"),

                // Implements & Attachments
                new ProductSeed("Tractor Trailer 2 Ton", "Heavy-duty trailer for transport.", 8990.00, 14, "Balwan", "unit", "India", "2 ton payload, hydraulic tipping optional", null, SRC_KHETIGAADI, "Implements & Attachments"),
                new ProductSeed("Front End Loader Attachment", "Loader for lifting and material handling.", 21990.00, 5, "Mahindra", "unit", "India", "Lift height 3.2 m, bucket 0.35 m³", null, SRC_MAHINDRA, "Implements & Attachments"),
                new ProductSeed("Seed Drill 9 Row", "Uniform seed placement with depth control.", 11990.00, 8, "Fieldking", "unit", "India", "9 row, fertilizer box, tractor 45+ HP", null, SRC_TRACTOR_JUNCTION, "Implements & Attachments"),
                new ProductSeed("Boom Sprayer 400L (PTO)", "PTO sprayer for large coverage spraying.", 6990.00, 12, "ASPEE", "unit", "India", "400L tank, 12m boom, brass nozzles", null, SRC_AGROSTAR, "Implements & Attachments"),

                // Spare Parts
                new ProductSeed("Tractor Oil Filter (Universal)", "Engine oil filter for common tractor models.", 99.90, 300, "BOSCH", "piece", "Germany", "High filtration media, anti-drain valve", null, SRC_INDIAMART, "Spare Parts"),
                new ProductSeed("Hydraulic Filter Element", "Hydraulic system filter element.", 149.90, 240, "MANN", "piece", "Germany", "10 micron filtration, long service life", null, SRC_TRADEINDIA, "Spare Parts"),
                new ProductSeed("Rotavator Blade Set (36 pcs)", "Heavy-duty blades set for 5 ft rotavator.", 1290.00, 70, "Shaktiman", "set", "India", "Boron steel, heat treated, left+right blades", null, SRC_ALIBABA, "Spare Parts"),
                new ProductSeed("Sprayer Nozzle Kit (20 pcs)", "Nozzle kit for boom sprayers.", 199.90, 120, "Lechler", "kit", "Germany", "Flat-fan nozzles, anti-drip, assorted sizes", null, SRC_MADEINCHINA, "Spare Parts"),
                new ProductSeed("V-Belt (B Section) 42 inch", "Drive belt for farm machinery.", 69.90, 500, "Gates", "piece", "USA", "Oil & heat resistant, tensile cord", null, SRC_INDIAMART, "Spare Parts"),

                // Safety & Wearables
                new ProductSeed("Pesticide Respirator Mask", "Reusable respirator for farm spraying.", 149.90, 180, "3M", "piece", "USA", "Activated carbon filters, adjustable straps", null, SRC_INDIAMART, "Safety & Wearables"),
                new ProductSeed("Safety Goggles (Anti-Fog)", "Eye protection for spraying and workshop.", 69.90, 220, "Uvex", "piece", "Germany", "Anti-fog lens, impact resistant", null, SRC_TRADEINDIA, "Safety & Wearables"),
                new ProductSeed("Heavy Duty Gloves (Nitrile)", "Chemical resistant gloves for farm use.", 74.90, 240, "Ansell", "pair", "USA", "Nitrile, long cuff, textured grip", null, SRC_INDIAMART, "Safety & Wearables"),
                new ProductSeed("Ear Protection (Noise Reduction)", "Hearing protection for machinery operation.", 89.90, 200, "Honeywell", "piece", "USA", "NRR 28 dB, padded headband", null, SRC_TRADEINDIA, "Safety & Wearables")
        );

        products.forEach(seed -> {
            Product product = new Product();
            product.setName(seed.name());
            product.setDescription(seed.description());
            product.setPrice(BigDecimal.valueOf(seed.price()));
            product.setStock(seed.stock());
            product.setBrand(seed.brand());
            product.setUnit(seed.unit());
            product.setOrigin(seed.origin());
            product.setSpecifications(seed.specifications());
            product.setImageUrl(seed.imageUrl());
            product.setSourceUrl(seed.sourceUrl());
            product.setCategory(categoryMap.get(seed.categoryKey()));
            productRepository.save(product);
        });
    }

    private void addCategory(Map<String, Category> categoryMap, String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        categoryRepository.save(category);
        categoryMap.put(name, category);
    }

    private record ProductSeed(String name, String description, double price, int stock, String brand,
                               String unit, String origin, String specifications, String imageUrl, String sourceUrl,
                               String categoryKey) {
    }
}
