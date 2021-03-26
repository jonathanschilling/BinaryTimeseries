#ifndef BINARYTIMESERIES_H
#define BINARYTIMESERIES_H

#include <algorithm>
#include <fstream>
#include <iostream>

#include <QDebug>
#include <QString>

// from https://stackoverflow.com/questions/3823921/convert-big-endian-to-little-endian-when-reading-from-a-binary-file
template <class T>
void endswap(T *objp)
{
  unsigned char *memp = reinterpret_cast<unsigned char*>(objp);
  std::reverse(memp, memp + sizeof(T));
}


class BinaryTimeseries
{
public:
    BinaryTimeseries(const QString& filename);
    ~BinaryTimeseries();

    int get_num_samples();

    long* get_timestamps_as_long(const long& rescale_timebase = 1);
    double* get_timestamps_as_double(const double& rescale_timebase = 1.0);

    double* get_scaled_as_double(const double& rescale_data = 1.0);

private:
    std::ifstream bts;

    bool swap_bytes;

    int8_t dtype_time;
    double t0_d, dt_d;
    int64_t   t0_l, dt_l;

    int8_t dtype_scaling;
    int8_t   o_b, s_b;
    int16_t  o_s, s_s;
    int32_t    o_i, s_i;
    int64_t   o_l, s_l;
    float  o_f, s_f;
    double o_d, s_d;

    int8_t dtype_raw;

    int32_t num_samples;

    int dtype_size(char dtype) {
        if        (dtype == 1) {
            return 1;
        } else if (dtype == 2) {
            return 2;
        } else if (dtype == 3) {
            return 4;
        } else if (dtype == 4) {
            return 8;
        } else if (dtype == 5) {
            return 4;
        } else if (dtype == 6) {
            return 8;
        } else {
            throw std::runtime_error("size of dtype "+std::to_string(dtype)+" not well-defined.");
        }
    }

};

#endif // BINARYTIMESERIES_H
