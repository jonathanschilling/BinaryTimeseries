#include "binarytimeseries.h"

BinaryTimeseries::BinaryTimeseries(const QString& filename)
{
    qDebug() << "open BinaryTimeseries from " << filename;

    // open the file
    bts = std::ifstream(filename.toStdString(), std::ios::in | std::ios::binary);

    // read endianess short, should be 1(endianess ok) or 256 (wrong endianess)
    int16_t endianessCheck = 0;
    bts.read((char*) &endianessCheck, 2);

    qDebug() << "endianessCheck = " << endianessCheck;

    if (endianessCheck == 1 || endianessCheck == 256) {
        swap_bytes = (endianessCheck == 256);
        if (swap_bytes) {
            qDebug() << "swap bytes during reading";
        }
    } else {
        throw std::runtime_error("endianessCheck should be 1 or 256, but not "+std::to_string(endianessCheck));
    }

    // dtype_time byte
    bts.read((char*)&dtype_time, 1);

    if (dtype_time == 4) {
        // long t0, dt
        bts.read((char*)&t0_l, 8);
        bts.read((char*)&dt_l, 8);
        if (swap_bytes) {
            endswap(&t0_l);
            endswap(&dt_l);
        }

        qDebug() << "t0 = " << t0_l;
        qDebug() << "dt = " << dt_l;
    } else if (dtype_time == 6) {
        // double t0, dt
        bts.read((char*)&t0_d, 8);
        bts.read((char*)&dt_d, 8);
        if (swap_bytes) {
            endswap(&t0_d);
            endswap(&dt_d);
        }
        qDebug() << "t0 =" << t0_d;
        qDebug() << "dt =" << dt_d;
    } else {
        throw std::runtime_error("dtype_time should be 4 or 6, but not "+std::to_string(dtype_time));
    }

    // dtype_scaling byte
    bts.read((char*)&dtype_scaling, 1);

    // offset and scaling; each up to bytes
    if        (dtype_scaling == 0) {
        // no scaling
        bts.seekg(16, std::ios_base::cur);
        qDebug() << "no scaling";
    } else if (dtype_scaling == 1) {
        // byte
        bts.read((char*)&o_b, 1); bts.seekg(7, std::ios_base::cur);
        bts.read((char*)&s_b, 1); bts.seekg(7, std::ios_base::cur);
        qDebug() << "offset =" << o_b;
        qDebug() << " scale =" << s_b;
    } else if (dtype_scaling == 2) {
        // short
        bts.read((char*)&o_s, 2); bts.seekg(6, std::ios_base::cur);
        bts.read((char*)&s_s, 2); bts.seekg(6, std::ios_base::cur);
        if (swap_bytes) {
            endswap(&o_s);
            endswap(&s_s);
        }
        qDebug() << "offset =" << o_s;
        qDebug() << " scale =" << s_s;
    } else if (dtype_scaling == 3) {
        // int
        bts.read((char*)&o_i, 4); bts.seekg(4, std::ios_base::cur);
        bts.read((char*)&s_i, 4); bts.seekg(4, std::ios_base::cur);
        if (swap_bytes) {
            endswap(&o_i);
            endswap(&s_i);
        }
        qDebug() << "offset =" << o_i;
        qDebug() << " scale =" << s_i;
    } else if (dtype_scaling == 4) {
        // long
        bts.read((char*)&o_l, 8);
        bts.read((char*)&s_l, 8);
        if (swap_bytes) {
            endswap(&o_l);
            endswap(&s_l);
        }
        qDebug() << "offset =" << o_l;
        qDebug() << " scale =" << s_l;
    } else if (dtype_scaling == 5) {
        // float
        bts.read((char*)&o_f, 4); bts.seekg(4, std::ios_base::cur);
        bts.read((char*)&s_f, 4); bts.seekg(4, std::ios_base::cur);
        if (swap_bytes) {
            endswap(&o_f);
            endswap(&s_f);
        }
        qDebug() << "offset =" << o_f;
        qDebug() << " scale =" << s_f;
    } else if (dtype_scaling == 6) {
        // double
        bts.read((char*)&o_d, 8);
        bts.read((char*)&s_d, 8);
        if (swap_bytes) {
            endswap(&o_d);
            endswap(&s_d);
        }
        qDebug() << "offset =" << o_d;
        qDebug() << " scale =" << s_d;
    } else {
        throw std::runtime_error("dtype_scaling should be 0 ... 6, but not "+std::to_string(dtype_scaling));
    }

    // reserved --> 23 bytes
    bts.seekg(23, std::ios_base::cur);

    // dtype_raw byte
    bts.read((char*)&dtype_raw, 1);
    if (dtype_raw < 1 || dtype_raw > 6) {
        throw std::runtime_error("dtype_raw should be 1 ... 6, but not "+std::to_string(dtype_raw));
    }

    // num_samples int
    bts.read((char*)&num_samples, 4);
    if (swap_bytes) {
        endswap(&num_samples);
    }
    if (num_samples < 1) {
        throw std::runtime_error("num_samples should be > 0, but not "+std::to_string(num_samples));
    }

    qDebug() << "number of samples = " << num_samples;

    // should be at position 64 in the file now; raw data comes after this
    int fpos = bts.tellg();
    if (fpos != 64) {
        throw std::runtime_error("should be at position 64 in the file after header, but tellg() gives "+std::to_string(fpos));
    }
}

int BinaryTimeseries::get_num_samples() {
    return num_samples;
}

long* BinaryTimeseries::get_timestamps_as_long(const long& rescale_timebase) {
    long *timestamps = (long*)malloc(num_samples*sizeof(long));

    // check if allocation was successful
    if (timestamps != NULL) {
        // allocation was successful, so compute timestamps

        if (rescale_timebase == 1) {
            if (dtype_time == 4) {
                // timestamps in long
                for (int i=0; i<num_samples; ++i) {
                    timestamps[i] = t0_l + i*dt_l;
                }
            } else if (dtype_time == 6) {
                // timestamps in double
                for (int i=0; i<num_samples; ++i) {
                    timestamps[i] = t0_d + i*dt_d;
                }
            }
        } else {
            if (dtype_time == 4) {
                // timestamps in long
                for (int i=0; i<num_samples; ++i) {
                    timestamps[i] = rescale_timebase*(t0_l + i*dt_l);
                }
            } else if (dtype_time == 6) {
                // timestamps in double
                for (int i=0; i<num_samples; ++i) {
                    timestamps[i] = rescale_timebase*(t0_d + i*dt_d);
                }
            }
        }

    } else {
        // allocation failed
        throw std::runtime_error("not enough memory to allocate full timeseries data vector as long");
    }

    return timestamps;
}

double* BinaryTimeseries::get_timestamps_as_double(const double& rescale_timebase) {
    double *timestamps = (double*)malloc(num_samples*sizeof(double));

    // check if allocation was successful
    if (timestamps != NULL) {
        // allocation was successful, so compute timestamps

        if (rescale_timebase == 1.0) {
            if (dtype_time == 4) {
                // timestamps in long
                for (int i=0; i<num_samples; ++i) {
                    timestamps[i] = t0_l + i*dt_l;
                }
            } else if (dtype_time == 6) {
                // timestamps in double
                for (int i=0; i<num_samples; ++i) {
                    timestamps[i] = t0_d + i*dt_d;
                }
            }
        } else {
            if (dtype_time == 4) {
                // timestamps in long
                for (int i=0; i<num_samples; ++i) {
                    timestamps[i] = rescale_timebase*(t0_l + i*dt_l);
                }
            } else if (dtype_time == 6) {
                // timestamps in double
                for (int i=0; i<num_samples; ++i) {
                    timestamps[i] = rescale_timebase*(t0_d + i*dt_d);
                }
            }
        }

    } else {
        // allocation failed
        throw std::runtime_error("not enough memory to allocate full timeseries data vector as double");
    }

    return timestamps;
}

double* BinaryTimeseries::get_scaled_as_double(const double& rescale_data) {
    double *scaled = (double*)malloc(num_samples*sizeof(double));

    // check if allocation was successful
    if (scaled != NULL) {
        // allocation was successful, so start reading data

        // go to beginning of raw data segment in the file
        bts.seekg(64);

        if (dtype_raw == 1) {
            // raw data are int8_t
            int8_t raw = 0;

            if (dtype_scaling == 0) {
                // no scaling
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 1);
                    scaled[i] = rescale_data * (raw);
                }
            } else if (dtype_scaling == 1) {
                // scaling is int8_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 1);
                    scaled[i] = rescale_data * (o_b + s_b * raw);
                }
            } else if (dtype_scaling == 2) {
                // scaling is int16_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 1);
                    scaled[i] = rescale_data * (o_s + s_s * raw);
                }
            } else if (dtype_scaling == 3) {
                // scaling is int32_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 1);
                    scaled[i] = rescale_data * (o_i + s_i * raw);
                }
            } else if (dtype_scaling == 4) {
                // scaling is int64_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 1);
                    scaled[i] = rescale_data * (o_l + s_l * raw);
                }
            } else if (dtype_scaling == 5) {
                // scaling is float
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 1);
                    scaled[i] = rescale_data * (o_f + s_f * raw);
                }
            } else if (dtype_scaling == 6) {
                // scaling is double
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 1);
                    scaled[i] = rescale_data * (o_d + s_d * raw);
                }
            }
        } else if (dtype_raw == 2) {
            // raw data are int16_t
            int16_t raw = 0;

            if (dtype_scaling == 0) {
                // no scaling
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 2);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (raw);
                }
            } else if (dtype_scaling == 1) {
                // scaling is int8_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 2);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_b + s_b * raw);
                }
            } else if (dtype_scaling == 2) {
                // scaling is int16_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 2);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_s + s_s * raw);
                }
            } else if (dtype_scaling == 3) {
                // scaling is int32_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 2);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_i + s_i * raw);
                }
            } else if (dtype_scaling == 4) {
                // scaling is int64_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 2);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_l + s_l * raw);
                }
            } else if (dtype_scaling == 5) {
                // scaling is float
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 2);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_f + s_f * raw);
                }
            } else if (dtype_scaling == 6) {
                // scaling is double
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 2);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_d + s_d * raw);
                }
            }
        } else if (dtype_raw == 3) {
            // raw data are int32_t
            int32_t raw = 0;

            if (dtype_scaling == 0) {
                // no scaling
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (raw);
                }
            } else if (dtype_scaling == 1) {
                // scaling is int8_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_b + s_b * raw);
                }
            } else if (dtype_scaling == 2) {
                // scaling is int16_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_s + s_s * raw);
                }
            } else if (dtype_scaling == 3) {
                // scaling is int32_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_i + s_i * raw);
                }
            } else if (dtype_scaling == 4) {
                // scaling is int64_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_l + s_l * raw);
                }
            } else if (dtype_scaling == 5) {
                // scaling is float
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_f + s_f * raw);
                }
            } else if (dtype_scaling == 6) {
                // scaling is double
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_d + s_d * raw);
                }
            }
        } else if (dtype_raw == 4) {
            // raw data are int64_t
            int64_t raw = 0;

            if (dtype_scaling == 0) {
                // no scaling
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (raw);
                }
            } else if (dtype_scaling == 1) {
                // scaling is int8_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_b + s_b * raw);
                }
            } else if (dtype_scaling == 2) {
                // scaling is int16_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_s + s_s * raw);
                }
            } else if (dtype_scaling == 3) {
                // scaling is int32_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_i + s_i * raw);
                }
            } else if (dtype_scaling == 4) {
                // scaling is int64_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_l + s_l * raw);
                }
            } else if (dtype_scaling == 5) {
                // scaling is float
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_f + s_f * raw);
                }
            } else if (dtype_scaling == 6) {
                // scaling is double
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_d + s_d * raw);
                }
            }
        } else if (dtype_raw == 5) {
            // raw data are float
            float raw = 0;

            if (dtype_scaling == 0) {
                // no scaling
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (raw);
                }
            } else if (dtype_scaling == 1) {
                // scaling is int8_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_b + s_b * raw);
                }
            } else if (dtype_scaling == 2) {
                // scaling is int16_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_s + s_s * raw);
                }
            } else if (dtype_scaling == 3) {
                // scaling is int32_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_i + s_i * raw);
                }
            } else if (dtype_scaling == 4) {
                // scaling is int64_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_l + s_l * raw);
                }
            } else if (dtype_scaling == 5) {
                // scaling is float
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_f + s_f * raw);
                }
            } else if (dtype_scaling == 6) {
                // scaling is double
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 4);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_d + s_d * raw);
                }
            }
        } else if (dtype_raw == 6) {
            // raw data are double
            double raw = 0;

            if (dtype_scaling == 0) {
                // no scaling
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (raw);
                }
            } else if (dtype_scaling == 1) {
                // scaling is int8_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_b + s_b * raw);
                }
            } else if (dtype_scaling == 2) {
                // scaling is int16_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_s + s_s * raw);
                }
            } else if (dtype_scaling == 3) {
                // scaling is int32_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_i + s_i * raw);
                }
            } else if (dtype_scaling == 4) {
                // scaling is int64_t
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_l + s_l * raw);
                }
            } else if (dtype_scaling == 5) {
                // scaling is float
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_f + s_f * raw);
                }
            } else if (dtype_scaling == 6) {
                // scaling is double
                for (int i=0; i<num_samples; ++i) {
                    bts.read((char*)&raw, 8);
                    if (swap_bytes) endswap(&raw);
                    scaled[i] = rescale_data * (o_d + s_d * raw);
                }
            }
        }

    } else {
        // allocation failed
        throw std::runtime_error("not enough memory to allocate full timeseries data vector as double");
    }

    return scaled;
}



BinaryTimeseries::~BinaryTimeseries()
{
    // close the file
    if (bts.is_open()) {
        bts.close();

        qDebug() << "closed BTS file";
    }
}
