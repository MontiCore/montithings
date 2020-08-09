#include "ColorsAdapter.h"
namespace montithings {
	namespace hierarchy {

		Colors::Color ColorsAdapter::convert(arma::vec element) {
			Colors::Color color;
			color.setI(element.at(0));
			return color;
		}

		arma::vec ColorsAdapter::convert(Colors::Color element) {
			arma::vec vector;
			double d = (double)element.getI();
			return vector = {d};
		}

	} // namespace hierarchy
} // namespace montithings
