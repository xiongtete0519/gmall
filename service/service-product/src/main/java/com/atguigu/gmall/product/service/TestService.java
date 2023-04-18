package com.atguigu.gmall.product.service;

public interface TestService {
    void testLock();

    //读锁测试
    String readLock();

    //写锁测试
    String writeLock();
}
