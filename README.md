# ANP Puller

Kotlin-based utility to pull fuel prices from ANP's website.

## Description

[ANP](https://www.gov.br/anp/pt-br) is the Brazilian agency responsible for regulating activities that integrate the
oil, natural gas, and biofuels industry <sup>[1]</sup>. Among the ANP's competencies, we can highlight its role in
collecting and making available relevant data on the prices practiced in fuels sales in Brazil. Such data are public so
that anyone can consult them through the [SLP (Price Research System)](https://preco.anp.gov.br/)
website, maintained by ANP <sup>[2]</sup>.

Although the SLP website meets the most basic usage expectations, it lacks means that make it possible to integrate
third-party software with the SLP, as there is no public API or structured data repository available. Thus, the SLP ends
up not being the most friendly solution for developers and researchers who intend to take better advantage of the data
held by ANP. For this reason, the `anp-puller` utility is being developed as part of the
[EzGas](https://github.com/AdoniasAlcantara/anp-puller-kt)
project to facilitate the extraction of fuel prices from the SLP website. This utility allows one to download all ANP's
fuel prices at once and in a few seconds, resulting in a large JSON file ready to be imported by database systems and
other similar software.

It's important to mention that the `anp-puller` utility is part of an independent project and has no relationship with
the ANP agency.

## Getting Started

Get the latest release of [anp-puller] and go to the [Usage](#usage) section, or continue to the next section to build
it from the source.

## Build

Building from the source with Gradle Wrapper is straightforward. Just run the command below in the root directory of the
repository.

For Linux or Mac:

```shell
./gradlew shadowJar
```

For Windows:

```
gradlew.bat shadowJar
```

After Gradle completes the build process, a JAR package is generated within the `./build/libs/` directory. For instance,
building version 1.0.0 results in the `./build/libs/anp-puller-1.0.0.jar` package.

## Usage

Before running `anp-puller.jar`, you need to provide the `config.json` configuration file and `cities.json` file
containing all the cities included in the search. For convenience, there are two templates for these files in
the [/assets](./assets) directory. Just copy them to the same directory where you placed the `anp-puller.jar` package
and modify them as needed.

### The `config.json` file:

This file contains the settings for running `anp-puller.jar`. Note that some properties are mandatory while others are
optional.

Properties:

| Name        | Required | Default                             | Description                                           |
| ----------- | :------: | ----------------------------------- | ----------------------------------------------------- |
| targetUrl   | Yes      | -                                   | URL used to send requests to ANP's website.           |
| cookieKey   | Yes      | -                                   | Key of the cookie used to access the ANP's website.   |
| cookieValue | Yes      | -                                   | Value of the cookie used to access the ANP's website. |
| weekCode    | Yes      | -                                   | Week number referring to ANP data collection.         |
| tempDir     | No       | System's default temp directory     | Directory where temporary files are stored.           |
| destFile    | No       | ./stations_yyyy-MM-dd_hh-mm-ss.json | Destination file containing the final result.         |
| citiesFile  | No       | ./cities.json                       | File containing the cities to be fetched.             |
| numWorkers  | No       | 4                                   | Number of simultaneously performed tasks.             |

Example:

```json
{
  "targetUrl": "http://preco.anp.gov.br/include/Relatorio_Excel_Resumo_Por_Municipio_Posto.asp",
  "cookieKey": "ASPSESSIONIDCQQRRBST",
  "cookieValue": "LHBOMOODCJFIIJJOPEACEDED",
  "weekCode": 9999
}
```

### The `cities.json` file:

An array of cities and their respective codes included in the search.

Properties of each entry:

| Name | Required | Default | Description      |
| ---- | :------: | ------- | ---------------- |
| code | Yes      | -       | The city's code. |
| name | Yes      | -       | The city's name. |

Example:

```json
[
  {
    "code": 988,
    "name": "Salvador"
  },
  {
    "code": 9668,
    "name": "São Paulo"
  },
  {
    "code": 7043,
    "name": "Rio de Janeiro"
  }
]
```

### Run

Run the following command to download and save fuel prices to the destination file.

```shell
java -jar anp-puller.jar
```

[1]: https://pt.wikipedia.org/wiki/Ag%C3%AAncia_Nacional_do_Petr%C3%B3leo,_G%C3%A1s_Natural_e_Biocombust%C3%ADveis

[2]: https://pt.wikipedia.org/wiki/Ag%C3%AAncia_Nacional_do_Petr%C3%B3leo,_G%C3%A1s_Natural_e_Biocombust%C3%ADveis#Pesquisa_de_pre%C3%A7os

[anp-puller]: https://github.com/AdoniasAlcantara/anp-puller-kt/releases/download/v1.0.0-RC1/anp-puller-1.0.0-RC1.jar
