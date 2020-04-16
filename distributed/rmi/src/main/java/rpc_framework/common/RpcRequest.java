package rpc_framework.common;

import java.io.Serializable;

public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 5980001873991069055L;
    private String className;
    private String methodName;
    private Object[] parameters;
    private String version;

    public RpcRequest() {
    }

    public RpcRequest(String className, String methodName, Object[] parameters, String version) {
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
        this.version = version;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

}
