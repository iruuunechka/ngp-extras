package ru.ifmo.ctd.ngp.demo.ffchooser.config.utils;

import java.util.HashMap;
import java.util.Map;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DynaConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.IdealConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.RConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.ConfigurationFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.DelayedFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.DynaFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.GreedyFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.IdealFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.NoLearnFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.RFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.*;

/**
 * Some utilities that provide mapping between the learning mode
 * and the concrete type of {@link Configuration}
 * 
 * @author Arina Buzdalova
 */
public class ConfigUtils {
	
	/**
	 * Returns map from configuration names to configuration factories
	 * @return map from configuration names to configuration factories
	 */
	public static Map<String, ConfigurationFactory<? extends Configuration>> getFactories() {
		Map<String, ConfigurationFactory<? extends Configuration>> map = new HashMap<>();
		
		ConfigurationFactory<IdealConfiguration> ideal = new IdealFactory();
		map.put(ideal.getName(), ideal);
		
		ConfigurationFactory<NoLearnConfiguration> noLearn = new NoLearnFactory();
		map.put(noLearn.getName(), noLearn);
		
		ConfigurationFactory<GreedyConfiguration> greedy = new GreedyFactory();
		map.put(greedy.getName(), greedy);
		
		ConfigurationFactory<DelayedConfiguration> delayed = new DelayedFactory();
		map.put(delayed.getName(), delayed);
		
		ConfigurationFactory<RConfiguration> average = new RFactory();
		map.put(average.getName(), average);
		
		ConfigurationFactory<DynaConfiguration> dyna = new DynaFactory();
		map.put(dyna.getName(), dyna);

        ConfigurationFactory<DynaWithStrategyConfiguration> dynaWithStrategy = new DynaWithStrategyFactory();
        map.put(dynaWithStrategy.getName(), dynaWithStrategy);

        ConfigurationFactory<PrioritizedSweepingConfiguration> prioritizedSweeping = new PrioritizedSweepingFactory();
        map.put(prioritizedSweeping.getName(), prioritizedSweeping);

        ConfigurationFactory<RLCDConfiguration> rlcd = new RLCDFactory();
        map.put(rlcd.getName(), rlcd);

        ConfigurationFactory<EpsQRLCDConfiguration> eqrlcd = new EpsQRLCDFactory();
        map.put(eqrlcd.getName(), eqrlcd);
		return map;
	}
}
