# IRext Win32 Example

A Windows desktop example demonstrating the IRext infrared decoder library.

## Overview

IRext is an open-source infrared remote control library. This example provides a command-line application to test IR decoding functionality on Windows.

## Features

- Decode IR binary files for air conditioners (AC)
- Decode IR binary files for TV devices
- Query supported AC modes, wind speeds, swing options, and temperature ranges
- Generate IR timing data for remote control signals

## Requirements

- Operating System: Windows 10 or later
- IDE: Visual Studio 2019 or later
- Build Tools: MSVC v142 (VS2019) or v143 (VS2022)

## Building

1. Open win32-example/IRextWin32Example.sln in Visual Studio.

2. If you encounter build error MSB8020 (Platform Toolset not found), retarget the solution:
   - Right-click the solution in Solution Explorer
   - Select Retarget solution
   - Choose an installed Windows SDK and Platform Toolset

3. Select the desired configuration (Debug or Release) and platform (x86 or x64).

4. Build the solution (Ctrl+Shift+B or Build > Build Solution).

## Usage

1. Run the compiled executable from the command line.
2. The program will prompt you to input key codes to simulate remote control actions.
3. Key code reference: https://site.irext.net/doc#keymap
4. Enter 99 to exit the program.

### Example Key Codes

| Key Code | Function |
|----------|----------|
| 14 | Query supported modes |
| 15 | Query supported wind speeds |
| 16 | Query temperature range |
| 99 | Exit program |

## Related Links

- IRext Official Site: https://irext.net
- IRext Documentation: https://site.irext.net/doc
- IRext GitHub: https://github.com/irext

## License

Please refer to the IRext repository for license information.
