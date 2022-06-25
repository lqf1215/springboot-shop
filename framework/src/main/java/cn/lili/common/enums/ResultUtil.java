package cn.lili.common.enums;


import cn.lili.common.vo.ResultMessage;

/**
 * 返回结果工具类
 *
 * @author lili
 */
public class ResultUtil<T> {

    /**
     * 抽象类，存放结果
     */
    private final ResultMessage<T> resultMessage;
    /**
     * 正常响应
     */
    private static final Integer SUCCESS = 1;

    /**
     * 显示 toast 当错误的时候判断1 显示
     */
    private static final Integer SHOW = 1;


    /**
     * 构造话方法，给响应结果默认值
     */
    public ResultUtil() {
        resultMessage = new ResultMessage<>();
        resultMessage.setSuccess(true);
        resultMessage.setMsg("success");
        resultMessage.setCode(SUCCESS);
        resultMessage.setShow(SHOW);
    }

    /**
     * 返回数据
     *
     * @param t 范型
     * @return 消息
     */
    public ResultMessage<T> setData(T t) {
        this.resultMessage.setData(t);
        this.resultMessage.setShow(SHOW);
        return this.resultMessage;
    }


    /**
     * 返回成功消息
     *
     * @param resultCode 返回码
     * @return 返回成功消息
     */
    public ResultMessage<T> setSuccessMsg(ResultCode resultCode) {
        this.resultMessage.setSuccess(true);
        this.resultMessage.setMsg(resultCode.message());
        this.resultMessage.setCode(resultCode.code());
        this.resultMessage.setShow(SHOW);
        return this.resultMessage;

    }

    /**
     * 抽象静态方法，返回结果集
     * @param t 范型
     * @param <T>  范型
     * @return 消息
     */
    public static <T> ResultMessage<T> data(T t) {
        return new ResultUtil<T>().setData(t);
    }

    /**
     * 返回成功
     *
     * @param resultCode 返回状态码
     * @return 消息
     */
    public static <T> ResultMessage<T> success(ResultCode resultCode) {
        return new ResultUtil<T>().setSuccessMsg(resultCode);
    }

    /**
     * 返回成功
     * @return 消息
     */
    public static <T> ResultMessage<T> success() {
        return new ResultUtil<T>().setSuccessMsg(ResultCode.SUCCESS);
    }

    /**
     * 返回失败
     *
     * @param resultCode 返回状态码
     * @return 消息
     */
    public static <T> ResultMessage<T> error(ResultCode resultCode) {
        return new ResultUtil<T>().setErrorMsg(resultCode);
    }

    /**
     * 返回失败
     *
     * @param code 状态码
     * @param msg  返回消息
     * @return 消息
     */
    public static <T> ResultMessage<T> error(Integer code, String msg) {
        return new ResultUtil<T>().setErrorMsg(code, msg);
    }

    /**
     * 服务器异常 追加状态码
     * @param resultCode 返回码
     * @return 消息
     */
    public ResultMessage<T> setErrorMsg(ResultCode resultCode) {
        this.resultMessage.setSuccess(false);
        this.resultMessage.setMsg(resultCode.message());
        this.resultMessage.setCode(resultCode.code());
        this.resultMessage.setShow(SHOW);
        return this.resultMessage;
    }

    /**
     * 服务器异常 追加状态码
     *
     * @param code 状态码
     * @param msg  返回消息
     * @return 消息
     */
    public ResultMessage<T> setErrorMsg(Integer code, String msg) {
        this.resultMessage.setSuccess(false);
        this.resultMessage.setMsg(msg);
        this.resultMessage.setCode(code);
        this.resultMessage.setShow(SHOW);
        return this.resultMessage;
    }

}
