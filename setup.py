import setuptools

with open("README.md", "r") as fh:
    long_description = fh.read()

setuptools.setup(
    name="BinaryTimeseries",
    version="1.0.3",
    author="Jonathan Schilling",
    author_email="jonathan.schilling@mail.de",
    description="A binary timeseries storage format, where the time axis is given via an expression.",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/jonathanschilling/BinaryTimeseries",
    packages=setuptools.find_packages(),
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: Apache Software License",
        "Operating System :: OS Independent",
    ],
    python_requires='>=3',
    install_requires=['numpy'],
)

