import { render, screen } from '@testing-library/react'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card'

describe('Card Components', () => {
  describe('Card', () => {
    test('should render children correctly', () => {
      render(
        <Card>
          <div>Test content</div>
        </Card>
      )
      
      expect(screen.getByText('Test content')).toBeInTheDocument()
    })

    test('should apply default classes', () => {
      render(
        <Card>
          <div>Test content</div>
        </Card>
      )
      
      const card = screen.getByText('Test content').parentElement
      expect(card).toHaveClass('bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden')
    })

    test('should apply custom className', () => {
      render(
        <Card className="custom-class">
          <div>Test content</div>
        </Card>
      )
      
      const card = screen.getByText('Test content').parentElement
      expect(card).toHaveClass('custom-class')
    })
  })

  describe('CardHeader', () => {
    test('should render children correctly', () => {
      render(
        <CardHeader>
          <div>Header content</div>
        </CardHeader>
      )
      
      expect(screen.getByText('Header content')).toBeInTheDocument()
    })

    test('should apply default classes', () => {
      render(
        <CardHeader>
          <div>Header content</div>
        </CardHeader>
      )
      
      const header = screen.getByText('Header content').parentElement
      expect(header).toHaveClass('px-6 py-4 border-b border-gray-100')
    })

    test('should apply custom className', () => {
      render(
        <CardHeader className="custom-header">
          <div>Header content</div>
        </CardHeader>
      )
      
      const header = screen.getByText('Header content').parentElement
      expect(header).toHaveClass('custom-header')
    })
  })

  describe('CardTitle', () => {
    test('should render children correctly', () => {
      render(
        <CardTitle>
          Title text
        </CardTitle>
      )
      
      expect(screen.getByText('Title text')).toBeInTheDocument()
    })

    test('should render as h3 element', () => {
      render(
        <CardTitle>
          Title text
        </CardTitle>
      )
      
      const title = screen.getByText('Title text')
      expect(title.tagName).toBe('H3')
    })

    test('should apply default classes', () => {
      render(
        <CardTitle>
          Title text
        </CardTitle>
      )
      
      const title = screen.getByText('Title text')
      expect(title).toHaveClass('text-lg font-semibold text-gray-800')
    })

    test('should apply custom className', () => {
      render(
        <CardTitle className="custom-title">
          Title text
        </CardTitle>
      )
      
      const title = screen.getByText('Title text')
      expect(title).toHaveClass('custom-title')
    })
  })

  describe('CardContent', () => {
    test('should render children correctly', () => {
      render(
        <CardContent>
          <div>Content text</div>
        </CardContent>
      )
      
      expect(screen.getByText('Content text')).toBeInTheDocument()
    })

    test('should apply default classes', () => {
      render(
        <CardContent>
          <div>Content text</div>
        </CardContent>
      )
      
      const content = screen.getByText('Content text').parentElement
      expect(content).toHaveClass('px-6 py-4')
    })

    test('should apply custom className', () => {
      render(
        <CardContent className="custom-content">
          <div>Content text</div>
        </CardContent>
      )
      
      const content = screen.getByText('Content text').parentElement
      expect(content).toHaveClass('custom-content')
    })
  })

  describe('Card Integration', () => {
    test('should render complete card structure', () => {
      render(
        <Card>
          <CardHeader>
            <CardTitle>Test Title</CardTitle>
          </CardHeader>
          <CardContent>
            <p>Test content paragraph</p>
          </CardContent>
        </Card>
      )
      
      expect(screen.getByText('Test Title')).toBeInTheDocument()
      expect(screen.getByText('Test content paragraph')).toBeInTheDocument()
    })

    test('should maintain proper structure hierarchy', () => {
      render(
        <Card>
          <CardHeader>
            <CardTitle>Test Title</CardTitle>
          </CardHeader>
          <CardContent>
            <p>Test content paragraph</p>
          </CardContent>
        </Card>
      )
      
      const title = screen.getByText('Test Title')
      const content = screen.getByText('Test content paragraph')
      
      // Check that both elements are rendered
      expect(title).toBeInTheDocument()
      expect(content).toBeInTheDocument()
      
      // Check that title is in an h3 element
      expect(title.tagName).toBe('H3')
    })
  })
})
