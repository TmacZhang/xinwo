性能监控组件通常都要依赖函数调用栈回溯，比如崩溃监控的崩溃栈、卡顿监控的耗时堆栈、内存监控的分配栈。目前`Android`原生栈回溯不能满足需求，具有以下几个缺点：

* `API`混乱，不同`Android`版本提供的栈回溯API不一致，数据结构不一致，32/64位的实现也不一致，编码复杂性大幅提高。
* 实现回溯功能的动态库也不断变化，`libcorkscrew.so->libunwind.so->libunwindstack.so`，且多为私有`API`，稳定性也得不到保障。
* 性能差，回溯一个线程的调用栈的耗时通常要达到毫秒级，完全不能满足卡顿和内存监控高频栈回溯的要求。
* 支持的功能不一致，也不支持配置，比如`Build ID`的获取，`java`调用栈的回溯。


``

    std::unique_ptr<Backtrace> backtrace(Backtrace::Create(BACKTRACE_CURRENT_PROCESS, gettid()));

    if (!backtrace->Unwind(0)) {
        ALOGW("%s: Failed to unwind callstack. %s", __FUNCTION__,
        backtrace->GetErrorString(backtrace->GetError()).c_str());
    }

    for (size_t i = 0; i < backtrace->NumFrames(); i++) {
        ALOGI("%s", backtrace->FormatFrameData(i).c_str());
    }
``
