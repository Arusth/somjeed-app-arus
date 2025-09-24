'use client';

import { useState } from 'react';
import type { FC } from 'react';

interface StarRatingProps {
  rating: number;
  onRatingChange: (rating: number) => void;
  size?: 'sm' | 'md' | 'lg';
  disabled?: boolean;
  showLabels?: boolean;
}

/**
 * StarRating component for collecting user feedback
 * Features interactive star selection with hover effects and labels
 */
export const StarRating: FC<StarRatingProps> = ({
  rating,
  onRatingChange,
  size = 'md',
  disabled = false,
  showLabels = true
}) => {
  const [hoverRating, setHoverRating] = useState<number>(0);

  const sizeClasses = {
    sm: 'w-5 h-5',
    md: 'w-6 h-6',
    lg: 'w-8 h-8'
  };

  const labels = [
    'Very Poor',
    'Poor', 
    'Average',
    'Good',
    'Excellent'
  ];

  const handleStarClick = (starRating: number) => {
    if (!disabled) {
      onRatingChange(starRating);
    }
  };

  const handleStarHover = (starRating: number) => {
    if (!disabled) {
      setHoverRating(starRating);
    }
  };

  const handleMouseLeave = () => {
    if (!disabled) {
      setHoverRating(0);
    }
  };

  const getStarColor = (starIndex: number) => {
    const currentRating = hoverRating || rating;
    if (starIndex <= currentRating) {
      return 'text-yellow-400 fill-current';
    }
    return 'text-gray-300';
  };

  return (
    <div className="flex flex-col items-center space-y-2">
      {/* Stars */}
      <div 
        className="flex space-x-1"
        onMouseLeave={handleMouseLeave}
      >
        {[1, 2, 3, 4, 5].map((starIndex) => (
          <button
            key={starIndex}
            type="button"
            className={`
              ${sizeClasses[size]}
              ${getStarColor(starIndex)}
              ${disabled ? 'cursor-not-allowed' : 'cursor-pointer hover:scale-110'}
              transition-all duration-150 ease-in-out
              focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-opacity-50 rounded
            `}
            onClick={() => handleStarClick(starIndex)}
            onMouseEnter={() => handleStarHover(starIndex)}
            disabled={disabled}
            aria-label={`Rate ${starIndex} out of 5 stars`}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              className="w-full h-full"
            >
              <path
                d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
              />
            </svg>
          </button>
        ))}
      </div>

      {/* Rating Label */}
      {showLabels && (hoverRating > 0 || rating > 0) && (
        <div className="text-sm text-gray-600 font-medium min-h-[20px] text-center">
          {labels[(hoverRating || rating) - 1]}
        </div>
      )}

      {/* Rating Count */}
      <div className="text-xs text-gray-500">
        {rating > 0 ? `${rating}/5 stars` : 'Click to rate'}
      </div>
    </div>
  );
};

export default StarRating;
