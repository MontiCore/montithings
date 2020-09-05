#pragma once
#include <string>
#include "Port.h"
#include <string>
#include <map>
#include <vector>
#include <list>
#include <set>
#include <armadillo>
#include "ColorsAdapterTOP.h"


namespace montithings {
	namespace hierarchy {

		class ColorsAdapter : ColorsAdapterTOP
		{
		private:
		public:
			ColorsAdapter() = default;
			Colors::Color convert(arma::vec element) override;
			arma::vec convert(Colors::Color element) override;
		};

	} // namespace hierarchy
} // namespace montithings
