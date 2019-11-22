package org.bbr.examples.ioc;


import static org.mockito.Mockito.spy;

public class FactorySpy {

    public static Factory getTestingFactory() {
        Factory injectSpy = spy(new Factory());
        Factory.injectSpy(injectSpy);
        return injectSpy;
    }

}