---
trigger: always_on
---

# Next.js 15 Server Actions & Form Handling Rules

## Core Architecture
* Use "use server" directive for inline or module-level Server Actions
* Implement proper FormData extraction and validation
* Handle both success and error states with proper return objects
* Use revalidatePath and revalidateTag for cache invalidation
* Support redirect after successful form submission
* Ensure Server Actions work with progressive enhancement

## Form Validation Patterns
* Create shared Zod schemas for client and server validation
* Implement server-side validation as primary security layer
* Add client-side validation for improved user experience
* Use useActionState for form state management and error display
* Handle field-level and form-level error messages
* Support both synchronous and asynchronous validation

## Progressive Enhancement
* Ensure forms work without JavaScript enabled
* Use next/form for enhanced form behavior (prefetching, client-side navigation)
* Implement proper loading states with pending indicators
* Support keyboard navigation and screen reader accessibility
* Handle form submission with and without client-side hydration
* Create fallback experiences for JavaScript failures

## useActionState Integration
* Replace deprecated useFormStatus with useActionState
* Manage form state, errors, and pending states effectively
* Handle initial state and state updates from Server Actions
* Display validation errors and success messages appropriately
* Support optimistic updates where beneficial
* Implement proper form reset after successful submission

## Error Handling & UX
* Provide clear, actionable error messages for validation failures
* Handle server errors gracefully with user-friendly messages
* Implement proper try/catch blocks in Server Actions
* Use error boundaries for unexpected failures
* Support field-level error display with proper ARIA attributes
* Create consistent error message patterns across forms

## Security & Performance
* Always validate data server-side regardless of client validation
* Sanitize and escape form inputs appropriately
* Implement CSRF protection (automatic with Server Actions)
* Use proper input validation and type checking
* Implement rate limiting for form submissions
* Use useOptimistic for immediate UI feedback
* Cache validation schemas and reuse across components