package net.bytebuddy.instrumentation;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.instrumentation.method.bytecode.bind.annotation.Default;
import net.bytebuddy.utility.JavaVersionRule;
import net.bytebuddy.utility.PrecompiledTypeClassLoader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.instrumentation.method.matcher.MethodMatchers.isDeclaredBy;
import static net.bytebuddy.instrumentation.method.matcher.MethodMatchers.not;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MethodDelegationDefaultTest extends AbstractInstrumentationTest {

    private static final String FOO = "foo", BAR = "bar";

    private static final String DEFAULT_INTERFACE = "net.bytebuddy.test.precompiled.DelegationDefaultInterface";

    private static final String DELEGATION_TARGET = "net.bytebuddy.test.precompiled.DelegationDefaultTarget";

    private static final String DELEGATION_TARGET_SERIALIZABLE = "net.bytebuddy.test.precompiled.DelegationDefaultTargetSerializable";

    @Rule
    public MethodRule java8Rule = new JavaVersionRule(8);

    private ClassLoader classLoader;

    @Before
    public void setUp() throws Exception {
        classLoader = new PrecompiledTypeClassLoader(getClass().getClassLoader());
    }

    @Test
    @JavaVersionRule.Enforce
    public void testDefaultInterface() throws Exception {
        DynamicType.Loaded<?> loaded = instrument(Object.class,
                MethodDelegation.to(classLoader.loadClass(DELEGATION_TARGET)),
                classLoader,
                not(isDeclaredBy(Object.class)),
                classLoader.loadClass(DEFAULT_INTERFACE));
        Object instance = loaded.getLoaded().newInstance();
        assertThat(instance.getClass().getDeclaredMethod(FOO).invoke(instance), is((Object) (FOO + BAR)));
    }

    @Test(expected = AbstractMethodError.class)
    @JavaVersionRule.Enforce
    public void testNoDefaultInterface() throws Exception {
        DynamicType.Loaded<?> loaded = instrument(Object.class,
                MethodDelegation.to(DelegationNoDefaultInterfaceInterceptor.class),
                classLoader,
                not(isDeclaredBy(Object.class)),
                DelegationNoDefaultInterface.class);
        DelegationNoDefaultInterface instance = (DelegationNoDefaultInterface) loaded.getLoaded().newInstance();
        instance.foo();
    }

    @Test
    @JavaVersionRule.Enforce
    public void testDefaultInterfaceSerializableProxy() throws Exception {
        DynamicType.Loaded<?> loaded = instrument(Object.class,
                MethodDelegation.to(classLoader.loadClass(DELEGATION_TARGET_SERIALIZABLE)),
                classLoader,
                not(isDeclaredBy(Object.class)),
                classLoader.loadClass(DEFAULT_INTERFACE));
        Object instance = loaded.getLoaded().newInstance();
        assertThat(instance.getClass().getDeclaredMethod(FOO).invoke(instance), is((Object) (FOO + BAR)));
    }

    public static interface DelegationNoDefaultInterface {

        String foo();
    }

    public static class DelegationNoDefaultInterfaceInterceptor {

        public static String intercept(@Default DelegationNoDefaultInterface proxy) {
            return proxy.foo();
        }
    }
}
