package net.bytebuddy.instrumentation.type;

import net.bytebuddy.instrumentation.method.bytecode.stack.StackSize;
import net.bytebuddy.utility.MockitoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class TypeListExplicitTest {

    private static final String FOO = "foo", BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription firstTypeDescription, secondTypeDescription;

    private TypeList typeList;

    @Before
    public void setUp() throws Exception {
        when(firstTypeDescription.getStackSize()).thenReturn(StackSize.SINGLE);
        when(secondTypeDescription.getStackSize()).thenReturn(StackSize.DOUBLE);
        typeList = new TypeList.Explicit(Arrays.asList(firstTypeDescription, secondTypeDescription));
    }

    @Test
    public void testRetrieval() throws Exception {
        assertThat(typeList.size(), is(2));
        assertThat(typeList.get(0), is(firstTypeDescription));
        assertThat(typeList.get(1), is(secondTypeDescription));
    }

    @Test
    public void testToInternalName() throws Exception {
        when(firstTypeDescription.getInternalName()).thenReturn(FOO);
        when(secondTypeDescription.getInternalName()).thenReturn(BAR);
        assertThat(typeList.toInternalNames(), is(new String[]{FOO, BAR}));
    }

    @Test
    public void testEmptyList() throws Exception {
        assertThat(new TypeList.Explicit(Collections.<TypeDescription>emptyList()).toInternalNames(), nullValue(String[].class));
    }

    @Test
    public void testSubList() throws Exception {
        assertThat(typeList.subList(0, 1), is((TypeList) new TypeList.Explicit(Arrays.asList(firstTypeDescription))));
    }

    @Test
    public void testStackSize() throws Exception {
        assertThat(typeList.getStackSize(), is(3));
    }
}
