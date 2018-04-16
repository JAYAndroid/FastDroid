package com.ylz.ehui.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;


/**
 * Implementation of MIME's Base64Utils encoding and decoding conversions.
 * Optimized code. (raw version taken from oreilly.jonathan.util,
 * and currently com.sun.org.apache.xerces.internal.ds.util.Base64Utils)
 *
 * @author Raul Benito(Of the xerces copy, and little adaptations).
 * @author Anli Shundi
 * @author Christian Geuer-Pollmann
 * @see <A HREF="ftp://ftp.isi.edu/in-notes/rfc2045.txt">RFC 2045</A>
 */
public class Base64Utils {

    /**
     * {@link java.util.logging} logging facility
     */
    static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Base64Utils.class.getName());


    /**
     * Field Base64UtilsDEFAULTLENGTH
     */
    public static final int Base64UtilsDEFAULTLENGTH = 76;

    /**
     * Field _Base64Utilslength
     */
    static int _Base64Utilslength = Base64Utils.Base64UtilsDEFAULTLENGTH;

    private Base64Utils() {
        // we don't allow instantiation
    }


    /**
     * Method decodeBigIntegerFromElement
     *
     * @param element
     * @return the biginter obtained from the node
     * @throws Exception
     */
    public static BigInteger decodeBigIntegerFromElement(Element element) throws Exception {
        return new BigInteger(1, Base64Utils.decode(element));
    }

    /**
     * Method decodeBigIntegerFromText
     *
     * @param text
     * @return the biginter obtained from the text node
     * @throws Exception
     */
    public static BigInteger decodeBigIntegerFromText(Text text) throws Exception {
        return new BigInteger(1, Base64Utils.decode(text.getData()));
    }


    /**
     * Method decode
     * <p>
     * Takes the <CODE>Text</CODE> children of the Element and interprets
     * them as input for the <CODE>Base64Utils.decode()</CODE> function.
     *
     * @param element
     * @return the byte obtained of the decoding the element
     * @throws Exception
     */
    public static byte[] decode(Element element) throws Exception {

        Node sibling = element.getFirstChild();
        StringBuffer sb = new StringBuffer();

        while (sibling != null) {
            if (sibling.getNodeType() == Node.TEXT_NODE) {
                Text t = (Text) sibling;

                sb.append(t.getData());
            }
            sibling = sibling.getNextSibling();
        }

        return decode(sb.toString());
    }


    /**
     * Method decode
     *
     * @param Base64Utils
     * @return the UTF bytes of the Base64Utils
     * @throws Exception
     */
    public static byte[] decode(byte[] Base64Utils) throws Exception {
        return decodeInternal(Base64Utils);
    }


    /**
     * Encode a byte array and fold lines at the standard 76th character.
     *
     * @param binaryData <code>byte[]<code> to be Base64Utils encoded
     * @return the <code>String<code> with encoded data
     */
    public static String encode(byte[] binaryData) {
        return encode(binaryData, Base64UtilsDEFAULTLENGTH);
    }

    static private final int BASELENGTH = 255;
    static private final int LOOKUPLENGTH = 64;
    static private final int TWENTYFOURBITGROUP = 24;
    static private final int EIGHTBIT = 8;
    static private final int SIXTEENBIT = 16;
    static private final int FOURBYTE = 4;
    static private final int SIGN = -128;
    static private final char PAD = '=';
    static private final boolean fDebug = false;
    static final private byte[] Base64UtilsAlphabet = new byte[BASELENGTH];
    static final private char[] lookUpBase64UtilsAlphabet = new char[LOOKUPLENGTH];

    static {

        for (int i = 0; i < BASELENGTH; i++) {
            Base64UtilsAlphabet[i] = -1;
        }
        for (int i = 'Z'; i >= 'A'; i--) {
            Base64UtilsAlphabet[i] = (byte) (i - 'A');
        }
        for (int i = 'z'; i >= 'a'; i--) {
            Base64UtilsAlphabet[i] = (byte) (i - 'a' + 26);
        }

        for (int i = '9'; i >= '0'; i--) {
            Base64UtilsAlphabet[i] = (byte) (i - '0' + 52);
        }

        Base64UtilsAlphabet['+'] = 62;
        Base64UtilsAlphabet['/'] = 63;

        for (int i = 0; i <= 25; i++)
            lookUpBase64UtilsAlphabet[i] = (char) ('A' + i);

        for (int i = 26, j = 0; i <= 51; i++, j++)
            lookUpBase64UtilsAlphabet[i] = (char) ('a' + j);

        for (int i = 52, j = 0; i <= 61; i++, j++)
            lookUpBase64UtilsAlphabet[i] = (char) ('0' + j);
        lookUpBase64UtilsAlphabet[62] = '+';
        lookUpBase64UtilsAlphabet[63] = '/';

    }

    protected static final boolean isWhiteSpace(byte octect) {
        return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
    }

    protected static final boolean isPad(byte octect) {
        return (octect == PAD);
    }


    /**
     * Encodes hex octects into Base64Utils
     *
     * @param binaryData Array containing binaryData
     * @return Encoded Base64Utils array
     */
    /**
     * Encode a byte array in Base64Utils format and return an optionally
     * wrapped line.
     *
     * @param binaryData <code>byte[]</code> data to be encoded
     * @param length     <code>int<code> length of wrapped lines; No wrapping if less than 4.
     * @return a <code>String</code> with encoded data
     */
    public static String encode(byte[] binaryData, int length) {

        if (length < 4) {
            length = Integer.MAX_VALUE;
        }

        if (binaryData == null)
            return null;

        int lengthDataBits = binaryData.length * EIGHTBIT;
        if (lengthDataBits == 0) {
            return "";
        }

        int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
        int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
        int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1 : numberTriplets;
        int quartesPerLine = length / 4;
        int numberLines = (numberQuartet - 1) / quartesPerLine;
        char encodedData[] = null;

        encodedData = new char[numberQuartet * 4 + numberLines];

        byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

        int encodedIndex = 0;
        int dataIndex = 0;
        int i = 0;
        if (fDebug) {
            System.out.println("number of triplets = " + numberTriplets);
        }

        for (int line = 0; line < numberLines; line++) {
            for (int quartet = 0; quartet < 19; quartet++) {
                b1 = binaryData[dataIndex++];
                b2 = binaryData[dataIndex++];
                b3 = binaryData[dataIndex++];

                if (fDebug) {
                    System.out.println("b1= " + b1 + ", b2= " + b2 + ", b3= " + b3);
                }

                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);

                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
                byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);

                if (fDebug) {
                    System.out.println("val2 = " + val2);
                    System.out.println("k4   = " + (k << 4));
                    System.out.println("vak  = " + (val2 | (k << 4)));
                }

                encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[(l << 2) | val3];
                encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[b3 & 0x3f];

                i++;
            }
            encodedData[encodedIndex++] = 0xa;
        }

        for (; i < numberTriplets; i++) {
            b1 = binaryData[dataIndex++];
            b2 = binaryData[dataIndex++];
            b3 = binaryData[dataIndex++];

            if (fDebug) {
                System.out.println("b1= " + b1 + ", b2= " + b2 + ", b3= " + b3);
            }

            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);

            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
            byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);

            if (fDebug) {
                System.out.println("val2 = " + val2);
                System.out.println("k4   = " + (k << 4));
                System.out.println("vak  = " + (val2 | (k << 4)));
            }

            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[val1];
            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[val2 | (k << 4)];
            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[(l << 2) | val3];
            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[b3 & 0x3f];
        }

        // form integral number of 6-bit groups
        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex];
            k = (byte) (b1 & 0x03);
            if (fDebug) {
                System.out.println("b1=" + b1);
                System.out.println("b1<<2 = " + (b1 >> 2));
            }
            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[val1];
            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[k << 4];
            encodedData[encodedIndex++] = PAD;
            encodedData[encodedIndex++] = PAD;
        } else if (fewerThan24bits == SIXTEENBIT) {
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);

            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[val1];
            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[val2 | (k << 4)];
            encodedData[encodedIndex++] = lookUpBase64UtilsAlphabet[l << 2];
            encodedData[encodedIndex++] = PAD;
        }

        //encodedData[encodedIndex] = 0xa;

        return new String(encodedData);
    }

    /**
     * Decodes Base64Utils data into octects
     *
     * @param encoded Byte array containing Base64Utils data
     * @return Array containind decoded data.
     * @throws Exception
     */
    public final static byte[] decode(String encoded) throws Exception {

        if (encoded == null)
            return null;

        return decodeInternal(encoded.getBytes("UTF-8"));
    }

    protected final static byte[] decodeInternal(byte[] Base64UtilsData) throws Exception {
        // remove white spaces
        int len = removeWhiteSpace(Base64UtilsData);

        if (len % FOURBYTE != 0) {
            throw new Exception("decoding.divisible.four");
            //should be divisible by four
        }

        int numberQuadruple = (len / FOURBYTE);

        if (numberQuadruple == 0)
            return new byte[0];

        byte decodedData[] = null;
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;


        int i = 0;
        int encodedIndex = 0;
        int dataIndex = 0;

        //decodedData      = new byte[ (numberQuadruple)*3];
        dataIndex = (numberQuadruple - 1) * 4;
        encodedIndex = (numberQuadruple - 1) * 3;
        //first last bits.
        b1 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
        b2 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
        if ((b1 == -1) || (b2 == -1)) {
            throw new Exception("decoding.general");//if found "no data" just return null
        }


        byte d3, d4;
        b3 = Base64UtilsAlphabet[d3 = Base64UtilsData[dataIndex++]];
        b4 = Base64UtilsAlphabet[d4 = Base64UtilsData[dataIndex++]];
        if ((b3 == -1) || (b4 == -1)) {
            //Check if they are PAD characters
            if (isPad(d3) && isPad(d4)) {               //Two PAD e.g. 3c[Pad][Pad]
                if ((b2 & 0xf) != 0)//last 4 bits should be zero
                    throw new Exception("decoding.general");
                decodedData = new byte[encodedIndex + 1];
                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
            } else if (!isPad(d3) && isPad(d4)) {               //One PAD  e.g. 3cQ[Pad]
                if ((b3 & 0x3) != 0)//last 2 bits should be zero
                    throw new Exception("decoding.general");
                decodedData = new byte[encodedIndex + 2];
                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            } else {
                throw new Exception("decoding.general");//an error  like "3c[Pad]r", "3cdX", "3cXd", "3cXX" where X is non data
            }
        } else {
            //No PAD e.g 3cQl
            decodedData = new byte[encodedIndex + 3];
            decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
            decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
        }
        encodedIndex = 0;
        dataIndex = 0;
        //the begin
        for (i = numberQuadruple - 1; i > 0; i--) {
            b1 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
            b2 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
            b3 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
            b4 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];

            if ((b1 == -1) ||
                    (b2 == -1) ||
                    (b3 == -1) ||
                    (b4 == -1)) {
                throw new Exception("decoding.general");//if found "no data" just return null
            }

            decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
            decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
        }
        return decodedData;
    }

    /**
     * Decodes Base64Utils data into  outputstream
     *
     * @param Base64UtilsData Byte array containing Base64Utils data
     * @param os              the outputstream
     * @throws IOException
     * @throws Exception
     */
    public final static void decode(byte[] Base64UtilsData,
                                    OutputStream os) throws Exception, IOException {
        // remove white spaces
        int len = removeWhiteSpace(Base64UtilsData);

        if (len % FOURBYTE != 0) {
            throw new Exception("decoding.divisible.four");
            //should be divisible by four
        }

        int numberQuadruple = (len / FOURBYTE);

        if (numberQuadruple == 0)
            return;

        //byte     decodedData[]      = null;
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;

        int i = 0;

        int dataIndex = 0;

        //the begin
        for (i = numberQuadruple - 1; i > 0; i--) {
            b1 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
            b2 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
            b3 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
            b4 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
            if ((b1 == -1) ||
                    (b2 == -1) ||
                    (b3 == -1) ||
                    (b4 == -1))
                throw new Exception("decoding.general");//if found "no data" just return null


            os.write((byte) (b1 << 2 | b2 >> 4));
            os.write((byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)));
            os.write((byte) (b3 << 6 | b4));
        }
        b1 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];
        b2 = Base64UtilsAlphabet[Base64UtilsData[dataIndex++]];

        //  first last bits.
        if ((b1 == -1) ||
                (b2 == -1)) {
            throw new Exception("decoding.general");//if found "no data" just return null
        }

        byte d3, d4;
        b3 = Base64UtilsAlphabet[d3 = Base64UtilsData[dataIndex++]];
        b4 = Base64UtilsAlphabet[d4 = Base64UtilsData[dataIndex++]];
        if ((b3 == -1) ||
                (b4 == -1)) {//Check if they are PAD characters
            if (isPad(d3) && isPad(d4)) {               //Two PAD e.g. 3c[Pad][Pad]
                if ((b2 & 0xf) != 0)//last 4 bits should be zero
                    throw new Exception("decoding.general");
                os.write((byte) (b1 << 2 | b2 >> 4));
            } else if (!isPad(d3) && isPad(d4)) {               //One PAD  e.g. 3cQ[Pad]
                if ((b3 & 0x3) != 0)//last 2 bits should be zero
                    throw new Exception("decoding.general");
                os.write((byte) (b1 << 2 | b2 >> 4));
                os.write((byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)));
            } else {
                throw new Exception("decoding.general");//an error  like "3c[Pad]r", "3cdX", "3cXd", "3cXX" where X is non data
            }
        } else {
            //No PAD e.g 3cQl
            os.write((byte) (b1 << 2 | b2 >> 4));
            os.write((byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)));
            os.write((byte) (b3 << 6 | b4));
        }
        return;
    }

    /**
     * Decodes Base64Utils data into  outputstream
     *
     * @param is containing Base64Utils data
     * @param os the outputstream
     * @throws IOException
     * @throws Exception
     */
    public final static void decode(InputStream is,
                                    OutputStream os) throws Exception, IOException {
        //byte     decodedData[]      = null;
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;

        int index = 0;
        byte[] data = new byte[4];
        int read;
        //the begin
        while ((read = is.read()) > 0) {
            byte readed = (byte) read;
            if (isWhiteSpace(readed)) {
                continue;
            }
            if (isPad(readed)) {
                data[index++] = readed;
                if (index == 3)
                    data[index++] = (byte) is.read();
                break;
            }


            if ((data[index++] = readed) == -1) {
                throw new Exception("decoding.general");//if found "no data" just return null
            }

            if (index != 4) {
                continue;
            }
            index = 0;
            b1 = Base64UtilsAlphabet[data[0]];
            b2 = Base64UtilsAlphabet[data[1]];
            b3 = Base64UtilsAlphabet[data[2]];
            b4 = Base64UtilsAlphabet[data[3]];

            os.write((byte) (b1 << 2 | b2 >> 4));
            os.write((byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)));
            os.write((byte) (b3 << 6 | b4));
        }


        byte d1 = data[0], d2 = data[1], d3 = data[2], d4 = data[3];
        b1 = Base64UtilsAlphabet[d1];
        b2 = Base64UtilsAlphabet[d2];
        b3 = Base64UtilsAlphabet[d3];
        b4 = Base64UtilsAlphabet[d4];
        if ((b3 == -1) ||
                (b4 == -1)) {//Check if they are PAD characters
            if (isPad(d3) && isPad(d4)) {               //Two PAD e.g. 3c[Pad][Pad]
                if ((b2 & 0xf) != 0)//last 4 bits should be zero
                    throw new Exception("decoding.general");
                os.write((byte) (b1 << 2 | b2 >> 4));
            } else if (!isPad(d3) && isPad(d4)) {               //One PAD  e.g. 3cQ[Pad]
                b3 = Base64UtilsAlphabet[d3];
                if ((b3 & 0x3) != 0)//last 2 bits should be zero
                    throw new Exception("decoding.general");
                os.write((byte) (b1 << 2 | b2 >> 4));
                os.write((byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)));
            } else {
                throw new Exception("decoding.general");//an error  like "3c[Pad]r", "3cdX", "3cXd", "3cXX" where X is non data
            }
        } else {
            //No PAD e.g 3cQl

            os.write((byte) (b1 << 2 | b2 >> 4));
            os.write((byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)));
            os.write((byte) (b3 << 6 | b4));
        }

        return;
    }

    /**
     * remove WhiteSpace from MIME containing encoded Base64Utils data.
     *
     * @param data the byte array of Base64Utils data (with WS)
     * @return the new length
     */
    protected static int removeWhiteSpace(byte[] data) {
        if (data == null)
            return 0;

        // count characters that's not whitespace
        int newSize = 0;
        int len = data.length;
        for (int i = 0; i < len; i++) {
            byte dataS = data[i];
            if (!isWhiteSpace(dataS))
                data[newSize++] = dataS;
        }
        return newSize;
    }
}
