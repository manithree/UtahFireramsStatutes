FROM rust:1.30 as build

WORKDIR /src/

RUN git clone https://github.com/manithree/UtahFireramsStatutes.git

WORKDIR /src/UtahFireramsStatutes/rust/utfastatutes

RUN cargo build --release

FROM gcr.io/distroless/cc

COPY --from=build /src/UtahFireramsStatutes/rust/utfastatutes/target/release/utfastatutes /

COPY --from=build /src/UtahFireramsStatutes/rust/utfastatutes/src/Settings.json /

CMD ["/utfastatutes"]