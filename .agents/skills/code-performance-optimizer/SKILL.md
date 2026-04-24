---
name: "code-performance-optimizer"
description: "Analyzes code structure and performance, identifies optimization opportunities, and provides improvement suggestions. Invoke when user wants to optimize code performance or improve code efficiency."
---

# Code Performance Optimizer

This skill helps optimize code performance by analyzing structure, identifying bottlenecks, and providing improvement recommendations.

## How It Works

1. **Structure Analysis**: Carefully analyze code structure and its purpose
2. **Structure Evaluation**: Assess the rationality of the current structure
3. **Structure Optimization**: If structure is unreasonable, return issues and corresponding optimization suggestions
4. **Detail Evaluation**: If structure is reasonable or has reasonable parts, evaluate code details
5. **Detail Optimization**: Return optimization opportunities in code details
6. **Permission-Based Modification**: Present optimization plan first, then ask for user permission before making any code changes

## Usage Guidelines

- **When to Invoke**: When user asks to optimize code performance, improve code efficiency, or identify performance bottlenecks
- **Scope**: Can analyze code in any language and any file size
- **Process**: Always provide optimization suggestions first, then wait for user approval before modifying any code
- **Output**: Clear, actionable optimization recommendations with explanations

## Example Workflow

1. User requests code performance optimization
2. Skill analyzes code structure and purpose
3. Skill evaluates current structure's rationality
4. Skill provides optimization recommendations
5. Skill asks for user permission to implement changes
6. Only if user approves, skill modifies code according to the plan

## Optimization Areas

- Algorithmic efficiency
- Memory usage
- I/O operations
- CPU utilization
- Code structure and organization
- Best practices implementation
- Performance bottlenecks identification

## Important Notes

- Never modify code without explicit user permission
- Always provide clear explanations for each optimization suggestion
- Focus on both structural improvements and micro-optimizations
- Consider trade-offs between performance and code readability
- Provide specific, measurable improvements when possible