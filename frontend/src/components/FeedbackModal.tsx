'use client';

import { useState } from 'react';
import type { FC } from 'react';
import StarRating from './StarRating';
import type { FeedbackRequest, BotProfile } from '@/types/chat';

interface FeedbackModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (feedback: FeedbackRequest) => Promise<void>;
  sessionId: string;
  userId: string;
  conversationTopic: string;
  conversationStartedAt: Date;
  messageCount: number;
  botProfile?: BotProfile;
}

/**
 * FeedbackModal component for collecting user satisfaction feedback
 * Features star rating, optional comment, and submission handling
 */
export const FeedbackModal: FC<FeedbackModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  sessionId,
  userId,
  conversationTopic,
  conversationStartedAt,
  messageCount,
  botProfile
}) => {
  const [rating, setRating] = useState<number>(0);
  const [comment, setComment] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const defaultBotProfile = {
    name: 'Somjeed',
    avatar: '🤖',
    color: '#22c55e'
  };

  const currentBotProfile = botProfile || defaultBotProfile;

  const handleSubmit = async () => {
    if (rating === 0) {
      setError('Please select a rating before submitting');
      return;
    }

    setIsSubmitting(true);
    setError('');

    try {
      const feedbackRequest: FeedbackRequest = {
        sessionId,
        userId,
        rating,
        comment: comment.trim() || undefined,
        conversationTopic,
        conversationStartedAt,
        conversationEndedAt: new Date(),
        messageCount,
        deviceType: getDeviceType(),
        conversationCompletedNaturally: true
      };

      await onSubmit(feedbackRequest);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to submit feedback');
    } finally {
      setIsSubmitting(false);
    }
  };

  const getDeviceType = (): string => {
    const userAgent = navigator.userAgent.toLowerCase();
    if (/mobile|android|iphone|ipad|phone/i.test(userAgent)) {
      return 'mobile';
    }
    if (/tablet|ipad/i.test(userAgent)) {
      return 'tablet';
    }
    return 'desktop';
  };

  const handleSkip = () => {
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center space-x-3">
            <div 
              className="w-10 h-10 rounded-full flex items-center justify-center text-white text-lg font-semibold"
              style={{ backgroundColor: currentBotProfile.color }}
            >
              {currentBotProfile.avatar}
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900">
                How was your experience?
              </h3>
              <p className="text-sm text-gray-500">
                Help us improve {currentBotProfile.name}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
            aria-label="Close feedback modal"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-6">
          {/* Rating Section */}
          <div className="text-center">
            <h4 className="text-md font-medium text-gray-900 mb-4">
              Please rate your conversation with {currentBotProfile.name}
            </h4>
            <StarRating
              rating={rating}
              onRatingChange={setRating}
              size="lg"
              disabled={isSubmitting}
            />
          </div>

          {/* Comment Section */}
          <div>
            <label htmlFor="feedback-comment" className="block text-sm font-medium text-gray-700 mb-2">
              Additional comments (optional)
            </label>
            <textarea
              id="feedback-comment"
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none text-gray-900 bg-white placeholder-gray-500 disabled:text-gray-600 disabled:bg-gray-50"
              style={{ color: '#111827', backgroundColor: '#ffffff' }} // Fallback inline styles
              placeholder="Tell us more about your experience..."
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              maxLength={1000}
              disabled={isSubmitting}
            />
            <div className="text-xs text-gray-500 mt-1 text-right">
              {comment.length}/1000 characters
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-md p-3">
              <div className="flex">
                <div className="flex-shrink-0">
                  <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                  </svg>
                </div>
                <div className="ml-3">
                  <p className="text-sm text-red-800">{error}</p>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex justify-between items-center p-6 border-t border-gray-200 bg-gray-50">
          <button
            onClick={handleSkip}
            className="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 transition-colors"
            disabled={isSubmitting}
          >
            Skip
          </button>
          <button
            onClick={handleSubmit}
            disabled={isSubmitting || rating === 0}
            className={`
              px-6 py-2 rounded-md text-sm font-medium transition-all duration-200
              ${rating > 0 && !isSubmitting
                ? 'bg-blue-600 hover:bg-blue-700 text-white shadow-sm hover:shadow-md'
                : 'bg-gray-300 text-gray-500 cursor-not-allowed'
              }
            `}
          >
            {isSubmitting ? (
              <div className="flex items-center space-x-2">
                <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                <span>Submitting...</span>
              </div>
            ) : (
              'Submit Feedback'
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default FeedbackModal;
