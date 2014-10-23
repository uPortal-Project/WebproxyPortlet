package uk.ac.ed.myed.cosign;

import java.util.Arrays;
import java.util.List;

import edu.wisc.my.webproxy.beans.interceptors.PreInterceptor;

/**
 * Pre-interceptor for extracting proxied credentials from the file on disk and
 * presenting them to remote web applications.
 */
public class CosignAndUserInfoPreInterceptor extends ChainOfPreInterceptor {
    private static final PreInterceptor[] interceptors = new PreInterceptor[] {
        new CosignPreInterceptor(),
        new edu.wisc.my.webproxy.beans.interceptors.UserInfoUrlParameterizingPreInterceptor()
    };

    @Override
    public List<? extends PreInterceptor> getPreInterceptors() {
        return Arrays.asList(interceptors);
    }
}
