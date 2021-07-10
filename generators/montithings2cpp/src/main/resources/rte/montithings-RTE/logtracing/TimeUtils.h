
#pragma once
#include <chrono>

namespace montithings {
    namespace logtracing {
        class Time
        {
        public:
            static long long
            getTSNanoseconds() {
                using namespace std::chrono;
                auto now = std::chrono::high_resolution_clock::now();
                auto timestamp = std::chrono::duration_cast<std::chrono::nanoseconds>(now.time_since_epoch());
                return timestamp.count();
            }
        };
    }
}
