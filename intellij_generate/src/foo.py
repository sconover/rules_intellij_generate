import argparse

# parser = argparse.ArgumentParser(
#     description='Extract the v2.2 config from a Docker image tarball.')

# parser.add_argument('--tarball', action='store', required=True,
#                     help=('The Docker image tarball from which to '
#                           'extract the image name.'))

# parser.add_argument('--output', action='store', required=True,
#                     help='The output file to which we write the config.')


def main():
    print("hello from foo-main")
#   args = parser.parse_args()

    # with docker_image.FromTarball(args.tarball) as img:
    #     with open(args.output, 'w') as f:
    #         f.write(img.config_file())


if __name__ == '__main__':
    main()