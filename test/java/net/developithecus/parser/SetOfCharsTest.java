/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class SetOfCharsTest {

    public SetOfCharsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of includeAll method, of class SetOfChars.
     */
    @Test
    public void testIncludeAll() {
        System.out.println("includeAll()");
        SetOfChars instance = new SetOfChars();
        String result = instance.includeAll().toString();
        assertEquals("0000-ffff;", result);
    }

    /**
     * Test of excludeAll method, of class SetOfChars.
     */
    @Test
    public void testExcludeAll() {
        System.out.println("excludeAll()");
        SetOfChars instance = new SetOfChars();
        String result = instance.excludeAll().toString();
        assertEquals("", result);
    }

    /**
     * Test of exlude method, of class SetOfChars.
     */
    @Test
    public void testExclude_char() {
        System.out.println("exlude(char)");
        SetOfChars instance = new SetOfChars();
        String result = instance.includeAll().toString();

        assertEquals("0000-ffff;", result);

        result = instance.exclude('\u0200').toString();
        assertEquals("0000-01ff;0201-ffff;", result);

        result = instance.exclude('\u0201').toString();
        assertEquals("0000-01ff;0202-ffff;", result);

        result = instance.exclude('\u0202').toString();
        assertEquals("0000-01ff;0203-ffff;", result);

        result = instance.exclude('\uffff').toString();
        assertEquals("0000-01ff;0203-fffe;", result);

        result = instance.exclude('\u0000').toString();
        assertEquals("0001-01ff;0203-fffe;", result);

        result = instance.exclude('\u0200').toString();
        assertEquals("0001-01ff;0203-fffe;", result);

        result = instance.exclude('\u0207').toString();
        assertEquals("0001-01ff;0203-0206;0208-fffe;", result);

        result = instance.exclude('\u01fe').toString();
        assertEquals("0001-01fd;01ff;0203-0206;0208-fffe;", result);
    }

    /**
     * Test of include method, of class SetOfChars.
     */
    @Test
    public void testInclude_char() {
        System.out.println("include(char)");
        SetOfChars instance = new SetOfChars();
        String result = instance.excludeAll().toString();

        assertEquals("", result);

        result = instance.include('\u0100').toString();
        assertEquals("0100;", result);

        result = instance.include('\uffff').toString();
        assertEquals("0100;ffff;", result);

        result = instance.include('\uffff').toString();
        assertEquals("0100;ffff;", result);

        result = instance.include('\ufffe').toString();
        assertEquals("0100;fffe-ffff;", result);

        result = instance.include('\ufffc').toString();
        assertEquals("0100;fffc;fffe-ffff;", result);

        result = instance.include('\ufffd').toString();
        assertEquals("0100;fffc-ffff;", result);

        result = instance.include('\u0102').toString();
        assertEquals("0100;0102;fffc-ffff;", result);

        result = instance.include('\u0101').toString();
        assertEquals("0100-0102;fffc-ffff;", result);

        result = instance.include('\u0104').toString();
        assertEquals("0100-0102;0104;fffc-ffff;", result);

        result = instance.include('\u0106').toString();
        assertEquals("0100-0102;0104;0106;fffc-ffff;", result);

        result = instance.include('\u0105').toString();
        assertEquals("0100-0102;0104-0106;fffc-ffff;", result);

        result = instance.include('\u0103').toString();
        assertEquals("0100-0106;fffc-ffff;", result);
    }

    /**
     * Test of include method, of class SetOfChars.
     */
    @Test
    public void testInclude_char_char() {
        System.out.println("include(char,char)");
        SetOfChars instance = new SetOfChars();

        String result = instance.excludeAll().toString();
        assertEquals("", result);

        result = instance.include('\u1000', '\u2000').toString();
        assertEquals("1000-2000;", result);

        result = instance.include('\u3000', '\u5000').toString();
        assertEquals("1000-2000;3000-5000;", result);

        result = instance.include('\u1500', '\u2500').toString();
        assertEquals("1000-2500;3000-5000;", result);

        result = instance.include('\u2500', '\u2505').toString();
        assertEquals("1000-2505;3000-5000;", result);

        result = instance.include('\u2506', '\u2510').toString();
        assertEquals("1000-2510;3000-5000;", result);

        result = instance.include('\u2f00', '\u3510').toString();
        assertEquals("1000-2510;2f00-5000;", result);

        result = instance.include('\u2a00', '\u2f00').toString();
        assertEquals("1000-2510;2a00-5000;", result);

        result = instance.include('\u2900', '\u29ff').toString();
        assertEquals("1000-2510;2900-5000;", result);

        result = instance.include('\u2500', '\u29ff').toString();
        assertEquals("1000-5000;", result);

        result = instance.include('\u6000', '\u6000').toString();
        assertEquals("1000-5000;6000;", result);

        result = instance.include('\u5001', '\u5fff').toString();
        assertEquals("1000-6000;", result);

        result = instance.include('\u7001', '\u7fff').toString();
        assertEquals("1000-6000;7001-7fff;", result);

        result = instance.include('\u6000', '\u7001').toString();
        assertEquals("1000-7fff;", result);

        result = instance.include('\u0100', '\u0200').toString();
        assertEquals("0100-0200;1000-7fff;", result);

        result = instance.include('\u00a0', '\u0205').toString();
        assertEquals("00a0-0205;1000-7fff;", result);

        result = instance.include('\u0005', '\u0fff').toString();
        assertEquals("0005-7fff;", result);

        result = instance.include('\u0100', '\u0105').toString();
        assertEquals("0005-7fff;", result);

        result = instance.include('\u0000', '\uffff').toString();
        assertEquals("0000-ffff;", result);
    }

    /**
     * Test of exclude method, of class SetOfChars.
     */
    @Test
    public void testExclude_char_char() {
        System.out.println("exclude(char,char)");
        SetOfChars instance = new SetOfChars();

        String result = instance.excludeAll().toString();
        assertEquals("", result);

        result = instance.exclude('\u0100', '\u0200').toString();
        assertEquals("", result);

        result = instance.includeAll().toString();
        assertEquals("0000-ffff;", result);

        result = instance.exclude('\u0100', '\u0200').toString();
        assertEquals("0000-00ff;0201-ffff;", result);

        result = instance.exclude('\u0050', '\u0210').toString();
        assertEquals("0000-004f;0211-ffff;", result);

        result = instance.exclude('\u0100', '\u0230').toString();
        assertEquals("0000-004f;0231-ffff;", result);

        result = instance.exclude('\u0020', '\u0100').toString();
        assertEquals("0000-001f;0231-ffff;", result);

        result = instance.exclude('\u0005', '\u001f').toString();
        assertEquals("0000-0004;0231-ffff;", result);

        result = instance.exclude('\u0000', '\u0004').toString();
        assertEquals("0231-ffff;", result);

        result = instance.exclude('\u0231', '\u0300').toString();
        assertEquals("0301-ffff;", result);

        result = instance.exclude('\u2000', '\uffff').toString();
        assertEquals("0301-1fff;", result);

        result = instance.exclude('\u0100', '\u0200').toString();
        assertEquals("0301-1fff;", result);

        result = instance.exclude('\u0100', '\u3fff').toString();
        assertEquals("", result);
    }

    /**
     * Test of contains method, of class SetOfChars.
     */
    @Test
    public void testContains() {
        System.out.println("contains(char)");
        SetOfChars instance = new SetOfChars();

        boolean result = instance.contains('\u0100');
        assertEquals(false, result);

        result = instance.includeAll().contains('\u0100');
        assertEquals(true, result);

        result = instance.exclude('\u0100').contains('\u0100');
        assertEquals(false, result);

        result = instance.exclude('\u0100', '\u0200').contains('\u0120');
        assertEquals(false, result);

        result = instance.contains('\u0020');
        assertEquals(true, result);
    }

}