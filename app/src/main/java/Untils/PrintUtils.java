package Untils;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/28 8:47
 * 修改人：donghaifeng
 * 修改时间：2017/9/28 8:47
 * 修改备注：打印机参数设置
 */

public class PrintUtils {

    /** * 复位打印机 */
    public static final byte[] RESET = {0x1b, 0x40};

    /** * 左对齐 */
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};

    /** * 中间对齐 */
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};

    /** * 右对齐 */
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};

    /** * 选择加粗模式 */
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};

    /** * 取消加粗模式 */
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};

    /** * 宽高加倍 */
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};

    /** * 宽加倍 */
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};

    /** * 高加倍 */
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};

    /** * 字体不放大 */
    public static final byte[] NORMAL = {0x1d, 0x21, 0x00};

    /** * 设置默认行间距 */
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};

    /** * 打印纸一行最大的字节 */
    private static final int LINE_BYTE_SIZE = 32;

    /** * 打印三列时，中间一列的中心线距离打印纸左侧的距离 */
    private static final int LEFT_LENGTH = 16;

    /** * 打印三列时，中间一列的中心线距离打印纸右侧的距离 */
    private static final int RIGHT_LENGTH = 16;

    /** * 打印三列时，第一列汉字最多显示几个文字 */
    private static final int LEFT_TEXT_MAX_LENGTH = 5;

}
