package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.Random;

import org.junit.Test;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.SingleDiffReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.SingleState;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators.AbsEvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators.SwitchPointEvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators.ParabolaEvaluatorsFactory;

import junit.framework.Assert;

/**
 * Tests that check whether configurations of different types
 * are serialized and deserialized in a proper way.
 * 
 * @author Arina Buzdalova
 */
public class SerializationTests {
	
	/**
	 * Tests serialization of {@link RConfiguration}
	 * @throws IOException if an I/O exception occurs
	 * @throws ClassNotFoundException if no definition for the class could be found
	 */
	@Test
    public void testDyna() throws IOException, ClassNotFoundException {
        testSerialization(new DynaConfiguration(genProperties(DynaConfiguration.class)));
    }
	
	/**
	 * Tests serialization of {@link RConfiguration}
	 * @throws IOException if an I/O exception occurs
	 * @throws ClassNotFoundException if no definition for the class could be found
	 */
	@Test
    public void testR() throws IOException, ClassNotFoundException {
        testSerialization(new RConfiguration(genProperties(RConfiguration.class)));
    }
	
	/**
	 * Tests serialization of {@link DelayedConfiguration}
	 * @throws IOException if an I/O exception occurs
	 * @throws ClassNotFoundException if no definition for the class could be found
	 */
	@Test
    public void testDelayed() throws IOException, ClassNotFoundException {
        testSerialization(new DelayedConfiguration(genProperties(DelayedConfiguration.class)));
    }

	/**
	 * Tests serialization of {@link GreedyConfiguration}
	 * @throws IOException if an I/O exception occurs
	 * @throws ClassNotFoundException if no definition for the class could be found
	 */
    @Test
    public void testGreedy() throws IOException, ClassNotFoundException {
    	testSerialization(new GreedyConfiguration(genProperties(GreedyConfiguration.class)));
    }

    /**
	 * Tests serialization of {@link IdealConfiguration}
	 * @throws IOException if an I/O exception occurs
	 * @throws ClassNotFoundException if no definition for the class could be found
	 */
    @Test
    public void testIdeal() throws IOException, ClassNotFoundException {
    	testSerialization(new IdealConfiguration(genProperties(IdealConfiguration.class)));
    }

    /**
	 * Tests serialization of {@link NoLearnConfiguration}
	 * @throws IOException if an I/O exception occurs
	 * @throws ClassNotFoundException if no definition for the class could be found
	 */
    @Test
    public void testNoLearn() throws IOException, ClassNotFoundException {
    	testSerialization(new NoLearnConfiguration(genProperties(NoLearnConfiguration.class)));
    }
	
	private void testSerialization(Configuration config) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(ba)) {
            out.writeObject(config);
        }

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(ba.toByteArray()))) {
            Object readConfig = in.readObject();
            Assert.assertEquals(config, readConfig);
		}
	}
	
	private Properties genProperties(Class<? extends Configuration> clazz) {
		Random r = new Random();
		Properties props = new Properties();    	
    	String[] keys = PropertiesUtils.getKeysFor(clazz);
    	
    	for (String key : keys) {
    		props.setProperty(key, String.valueOf(r.nextInt(10)));
    	}
    	String param = "(divisor = 10, length = 10, switchPoint = 2)";
    	
    	String[] evals = {
    			SwitchPointEvaluatorsFactory.class.getCanonicalName(),
    			AbsEvaluatorsFactory.class.getCanonicalName(),
    			ParabolaEvaluatorsFactory.class.getCanonicalName()};
    	
    	props.setProperty("evaluators", evals[r.nextInt(evals.length)] + param);
    	
    	props.setProperty("reward", SingleDiffReward.class.getCanonicalName() + "()");
    	
		props.setProperty("state", SingleState.class.getCanonicalName() + "()");
    	
    	return props;
	}
}
